package com.example.genetiicz.Service;

import com.example.genetiicz.DTO.ContentDTO;
import com.example.genetiicz.DTO.ProjectDTO;
import com.example.genetiicz.Entity.ContentEntity;
import com.example.genetiicz.Entity.ProjectEntity;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.Role;

import com.example.genetiicz.Exceptions.DuplicateProjectURLException;
import com.example.genetiicz.Exceptions.NotAuthorizedException;
import com.example.genetiicz.Exceptions.ProjectNotFoundException;
import com.example.genetiicz.Repository.ContentRepository;
import com.example.genetiicz.Repository.ProjectRepository;
import com.example.genetiicz.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private MultipartFile imageUrlProject;
    private ContentRepository contentRepository;

    //THESE ARE FOR UPLOADS AND SHOULD BE STATIC THROUGHOUT THE CLASS
    private static final long MAX_TOTAL_BYTES = 50L * 1024 * 1024; //equals 50MB.

    public ProjectService(
            ProjectRepository projectRepository, UserRepository userRepository,
            ContentRepository contentRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    //I want to add method for actual setting values for Project with DTO.

    public Long addProject(ProjectDTO projectDTO, String email) throws RoleNotFoundException {
        //there is a generatedValue so we don't need to set the id for the project
        //This is the same as user, but here we do this for project instead.
        //And we need also to save this, and this should actually set values for the user
        //that is authenticated.


        //I need a new reference variable for the new object
        ProjectEntity project = new ProjectEntity();

        //I need also to create a new object where i can map the user
        //to the correct project. *I have this by optional now in UserRepository*
        Optional <UserEntity> projectAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);

        //Need to store the object found in a boolean reference object to check it later
        //boolean checkDuplicate = projectRepository.existsByProjectURL(projectDTO.getProjectURL());

        if (!projectAdmin.isPresent()) { // i think the best way is an boolean to check if the presence is there so i can then map the project to the admin
            throw new RoleNotFoundException("No Admin here");
            }
            //these are the values that will be stored in the object.
            project.setProjectName(projectDTO.getProjectName());
            project.setProjectDescription(projectDTO.getProjectDescription());
            project.setKeywords(projectDTO.getKeywords()); //keywords will appear right after description

            String projectURL = projectDTO.getProjectURL();
            if(projectURL == null || projectURL.isBlank()) {
                project.setProjectURL(null);
            } else if (projectRepository.existsByProjectURL(projectURL)) {
                throw new DuplicateProjectURLException("Another project is reffered to this URL");
            } else {
                project.setProjectURL(projectURL);
            }
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());
            project.setActive(projectDTO.isActive()); // set the status of the project.
            project.setProjectCategory(projectDTO.getProjectCategory());
            project.setUserEntity(projectAdmin.get());

            // project.setProjectFile(projectDTO.getProjectFile());

            //Save the current project made based on the boolean object reference that checks so we can set values.
            ProjectEntity savedProject = projectRepository.save(project);
            System.out.print("Admin added: "  + projectAdmin.get().getFirstName() + " " + projectAdmin.get().getLastName() + "\n" +
                    "Project: " + project.getProjectName() + "\n " + project.getProjectDescription() + "\n " + project.getProjectURL());

        return savedProject.getProjectId();
    }

    public void updateProject(Long projectId, String email,ProjectDTO projectDTO) {

        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);
        if (seededAdmin.isEmpty()) {
            throw new NotAuthorizedException("Not authenticated for this request");
        }

        Optional <ProjectEntity> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        ProjectEntity updateProject = project.get();
        updateProject.setImagePath(projectDTO.getImagePath()); // the imageCover photo
        updateProject.setProjectName(projectDTO.getProjectName());
        updateProject.setProjectDescription(projectDTO.getProjectDescription());
        updateProject.setKeywords(projectDTO.getKeywords()); //set values for keywords and retrieve.

        String projectURL = projectDTO.getProjectURL();
        if (projectURL == null || projectURL.isBlank()) {
            updateProject.setProjectURL(null);
        } else if (projectRepository.existsByProjectURLAndProjectIdNot(projectURL, projectId)) {
            throw new DuplicateProjectURLException("Another project is referred to this URL, please use another one!");
        } else {
            updateProject.setProjectURL(projectURL);
        }

        updateProject.setStartDate(projectDTO.getStartDate());
        updateProject.setEndDate(projectDTO.getEndDate());
        updateProject.setActive(projectDTO.isActive());
        updateProject.setProjectCategory(projectDTO.getProjectCategory());

        projectRepository.save(updateProject); // update the project and save it to entity
    }

    public void deleteProject(Long projectId,String email) {
        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);
        if(seededAdmin.isEmpty()) {
            throw new NotAuthorizedException("Not Authorized to do this requests"); // i have been blind to this - it should be an exception for authentication and not authorization
        }

        Optional <ProjectEntity> project = projectRepository.findById(projectId);
        if(project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        ProjectEntity currentProject = project.get();

        //delete ever content that is currently related to the currentProject. So we need to iterate over a list with a for enhanced loop of ContentEntity
        List<ContentEntity> allContents =  contentRepository.findAllByProjectEntity_ProjectId(projectId);

        for(ContentEntity content : allContents) {
            Path contentPath = Paths.get(content.getFilePath());
            try {
                Files.deleteIfExists(contentPath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        contentRepository.deleteAll(allContents);

        if(currentProject.getImagePath() != null) {
            Path coverPath = Paths.get(currentProject.getImagePath());
            try {
                Files.deleteIfExists(coverPath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        projectRepository.deleteById(projectId);
    }

    //Now i want to fetch all projects for myself, so i can display this later in a frontend page.
    public List<ProjectDTO> getAllProjects() {


        //JPA query the whole lists
        List <ProjectEntity> adminProjects = projectRepository.findAll();
        List <ContentEntity> allContents = contentRepository.findAll();

        List <ProjectDTO> listOfProjects = new ArrayList<>();
        for (ProjectEntity projects : adminProjects) {

            ProjectDTO projectDTO = new ProjectDTO();
            //crucial: projectId must be retrieved, if not content will never know which project it is referred to.
            projectDTO.setProjectId(projects.getProjectId());
            projectDTO.setProjectName(projects.getProjectName());
            projectDTO.setProjectDescription(projects.getProjectDescription());
            projectDTO.setKeywords(projects.getKeywords()); // this should list the keywords, since they are in DB
            projectDTO.setProjectURL(projects.getProjectURL());
            projectDTO.setStartDate(projects.getStartDate());
            projectDTO.setEndDate(projects.getEndDate());
            projectDTO.setImagePath(projects.getImagePath());
            projectDTO.setActive(projects.isActive());
            projectDTO.setProjectCategory(projects.getProjectCategory());

            // i need a inner loop to match all contents provided related to project.
            //Found this out by testing and verifying that the content are not shown in JSON body fields

            List <ContentDTO> matchedContent = new ArrayList<>();
            for (ContentEntity content : allContents) {
                ContentDTO contentDTO = new ContentDTO();

                //i need to cross check that contents are pointed to projectid
                if(content.getProjectEntity().getProjectId().equals(projects.getProjectId())) {
                    contentDTO.setContentId(content.getContentId());
                    contentDTO.setFilePath(content.getFilePath());
                    contentDTO.setFileSize(content.getFileSize());
                    contentDTO.setContentType(content.getContentType());
                    matchedContent.add(contentDTO); // we add the contents to the projectId
                }
            }
            projectDTO.setContent(matchedContent);//arraylist for matchedContents and we set the values of all contents referred to their each project.
            listOfProjects.add(projectDTO);//arraylist object stored with all the elements
        }
        //Return the created list of the new Arraylist that stores all of these fields in the object.
        return listOfProjects;
    }
}
