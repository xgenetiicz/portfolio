package com.example.genetiicz.Controller;


import com.example.genetiicz.DTO.ProjectDTO;
import com.example.genetiicz.Repository.UserRepository;
import com.example.genetiicz.Service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.management.relation.RoleNotFoundException;
import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private ProjectService projectService;
    private UserRepository userRepository;

    public ProjectController (ProjectService projectService, UserRepository userRepository) {
        this.projectService = projectService; //this projectservice will get the new value of the projectservice
        //and then post this to the database through method on ProjectService.
        this.userRepository = userRepository; // this is added since i am referring to userId and i need to have it on my controller post endpoint
    }

    //We use http body param objekt to pass the object and then set values.
    @PostMapping("/addproject")
    public ResponseEntity <String> addProject(@Valid @RequestBody ProjectDTO projectDTO) throws RoleNotFoundException { //im checking the addproject now with ExceptiononRole
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); //by SecurityContextHolder, i get the context and also the authentication by Name, that holds the parameter and value as email.
        projectService.addProject(projectDTO,email);

        //We return to know if the method is successfully.
        return ResponseEntity.status(201).body("Project added successfully and also added\nProject: " + projectDTO.getProjectName()); //reveals project added on Postman. This is just an confirmation
        // that the request is working as it should.
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<String>deleteProject(@PathVariable Long projectId) throws RoleNotFoundException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        projectService.deleteProject(projectId,email);
        return ResponseEntity.ok("Project deleted successfully");
    }

    @GetMapping("/fetchProjects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> fetchedProjects = projectService.getAllProjects();
        return ResponseEntity.status(200).body(fetchedProjects);
    }

    @PutMapping("/update/{projectId}")
    public ResponseEntity<String> updateProject(@PathVariable Long projectId, @Valid @RequestBody ProjectDTO projectDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        projectService.updateProject(projectId,email,projectDTO);
        return ResponseEntity.ok("Project updated successfully");
    }
}
