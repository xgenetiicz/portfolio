package com.example.genetiicz.Service;

import com.example.genetiicz.DTO.ProjectDTO;
import com.example.genetiicz.Entity.ProjectEntity;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.Role;

import com.example.genetiicz.Exceptions.NotAuthorizedException;
import com.example.genetiicz.Exceptions.ProjectNotFoundException;
import com.example.genetiicz.Repository.ContentRepository;
import com.example.genetiicz.Repository.ProjectRepository;
import com.example.genetiicz.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.management.relation.RoleNotFoundException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public void addProject(ProjectDTO projectDTO, String email) throws RoleNotFoundException {
        //Want to print first how the user can actually add - for later implementation
        System.out.println("Click on '+ Add Project'\n So you can add the desired project!");
        //there is a generatedValue so we don't need to set the id for the project
        //This is the same as user, but here we do this for project instead.
        //And we need also to save this, and this should actually set values for the user
        //that is authenticated.


        //I need a new reference variable for the new object
        ProjectEntity project = new ProjectEntity();

        //I need also to create a new object where i can map the user
        //to the correct project. *I have this by optional now in UserRepository*
        Optional <UserEntity> projectAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);

        if (!projectAdmin.isPresent()) { // i think the best way is an boolean to check if the presence is there so i can then map the project to the admin
            throw new RuntimeException("*There is no ADMIN present at all*" + userRepository.findByRoleAndEmail(Role.USERS, email));
        } else if(userRepository.existsByRole(Role.ADMIN)) {
            //these are the values that will be stored in the object.
            project.setProjectName(projectDTO.getProjectName());
            project.setProjectDescription(projectDTO.getProjectDescription());
            project.setProjectURL(projectDTO.getProjectURL());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());
            project.setUserEntity(projectAdmin.get());

            // project.setProjectFile(projectDTO.getProjectFile());

            //Save the current project made based on the boolean object reference that checks so we can set values.
            projectRepository.save(project);
            System.out.print("Admin added: "  + projectAdmin.get().getFirstName() + " " + projectAdmin.get().getLastName() + "\n" +
                    "Project: " + project.getProjectName() + "\n " + project.getProjectDescription() + "\n " + project.getProjectURL());
        } else {
            throw new RoleNotFoundException("There are no Roles fetched, but Admin should have been fetched for method addProject(ProjectDTO projectDTO)");
        }
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
        updateProject.setProjectName(projectDTO.getProjectName());
        updateProject.setProjectDescription(projectDTO.getProjectDescription());
        updateProject.setProjectURL(projectDTO.getProjectURL());
        updateProject.setStartDate(projectDTO.getStartDate());
        updateProject.setEndDate(projectDTO.getEndDate());

        projectRepository.save(updateProject);
    }

    public void deleteProject(Long projectId,String email) {
        Optional <ProjectEntity> project = projectRepository.findById(projectId);
        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);

        if(seededAdmin.isPresent()) {
            if(project.isPresent()){
                projectRepository.deleteById(projectId);
            } else {
                throw new ProjectNotFoundException("Project not found");
            }
        } else {
            throw new NotAuthorizedException("Not authorized to do this request"); // This is actually useless- since securityconfig shouldn't let anyone pass through it
            //because the api endpoints requests that this path is authenticated by Role.ADMIN where this is my email - but defense in depth. hehe
        }
    }

    //Now i want to fetch all projects for myself, so i can display this later in a frontend page.
    public List<ProjectDTO> getAllProjects(String userName) throws AccountNotFoundException {

        //New Instance of list where we call on the repository to make the declarative query with JPA on userName instead.
        List <ProjectEntity> getUserNameProjects = projectRepository.findAllByUserEntity_UserName(userName);

        //List<ProjectEntity> projectEntity = projectRepository.findAllByUserEntity_Email(email);

        //I want to actually have this statement check with an inverted logic, if projectEntity is not Empty,
        //I want then to stream and map all the objects and place them In a new list with collection.
        if(!getUserNameProjects.isEmpty()) {
            return getUserNameProjects.stream().map(
                            project -> {
                                ProjectDTO projectDTO = new ProjectDTO();
                                projectDTO.setProjectName(project.getProjectName());
                                projectDTO.setProjectDescription(project.getProjectDescription());
                                projectDTO.setProjectURL(project.getProjectURL());
                                return projectDTO;
                            }).collect(Collectors.toList());
        }
        //I need to also check if the project list is actually empty, and i will check this by username instead since I don't compromize any email data.
        boolean accountExists = userRepository.existsByUsername(userName);

        //then I want to throw the Exception State.
        if(!accountExists){
            throw new AccountNotFoundException("Did not find associated account for the projects.");
            //else this user doesn't have any projects associated with the account
        } else {
            System.out.println("User has no projects available with the associated account");
            //and the returned value of the else statement should include the Collection of the empty list.
            return Collections.emptyList();
        }
    }
}
