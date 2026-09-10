package com.example.genetiicz.Service;


import com.example.genetiicz.DTO.ProjectDTO;
import com.example.genetiicz.Entity.ProjectEntity;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.Role;
import com.example.genetiicz.Repository.ProjectRepository;
import com.example.genetiicz.Repository.UserRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.management.relation.RoleNotFoundException;
import javax.security.auth.login.AccountNotFoundException;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private MultipartFile imageUrlProject;

    //Constructor example with autowired

    //@Autowired
    //public ProjectService projectService;

    //Constructor with this keyword
    //
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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

    /*
    So here is to add the business logic for the image file, the idea is to have this
    in a folder on my raspberry, where these will be stored there and also called on
    later when finding projectId, so one image should have a reference on projectId,
    and a projectId have an reference to userId because of @ManyToOne
     */

    //And i want return the object to the user.
    public String uploadProjectImage(Long projectId,Long userId, MultipartFile imageUrlProject ) throws FileUploadException { //one image to each projectId

        //So i genereate first random unique filenames
        String filename = UUID.randomUUID() + "_" + imageUrlProject.getOriginalFilename();

        //Then i need to store those files physically
        Path uploadPath = Paths.get("uploads/projects/" + projectId +"/");
        try {
            Files.createDirectories(uploadPath); //making dir for the actualpath where the files should be copied too.
            Files.copy(imageUrlProject.getInputStream(),uploadPath.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Cannot copy files and store these into uploadPath",e);
        }
        //The business logic and updating database
        /*
        So the logic should work in a way where when user create the project, and the user adds the desired image
        it should add this to the project. So at the time the method addProject() is called, this method should also be called in
        when adding image file
         */
        Optional<ProjectEntity> placeImageOnProject = projectRepository.findProjectByProjectIdAndUserEntity_UserId(projectId,userId);
        System.out.println("Project found: " + placeImageOnProject.isPresent()); //need to see if it finds the project
        System.out.println("projectId: " + projectId + "\nuserId: " + userId); // and the id of project and the user id pointed to projectId.
        if (placeImageOnProject.isPresent()) {
            ProjectEntity project = placeImageOnProject.get();
            project.setImagePath("uploads/projects/" + filename); //filename contains UUID.randomUUID() + "_" imageUrlProject.getOriginalFileName
            projectRepository.save(project);
            return "uploads/projects/" + projectId + "/" + filename; //Now it should be uploaded on projectId
        }
        throw new FileUploadException("The desired image is not uploaded. Please try again");
    }
}
