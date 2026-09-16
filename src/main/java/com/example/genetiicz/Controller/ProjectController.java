package com.example.genetiicz.Controller;


import com.example.genetiicz.DTO.ContentDTO;
import com.example.genetiicz.DTO.ProjectDTO;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Repository.UserRepository;
import com.example.genetiicz.Service.ProjectService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    //This is the first time I am implementing a @GetMapping,but I want to validate it with @RequestParam
    //because this pass the email as query parameter to a dedicated validation endpoint that queries with the database

    public ResponseEntity<List> getAllProjects(@RequestParam String userName) throws AccountNotFoundException {
        List<ProjectDTO> fetchedProjects = projectService.getAllProjects(userName);
        if(fetchedProjects.isEmpty()) {
            return ResponseEntity.status(404).build();
        } else {
            return ResponseEntity.status(200).body(fetchedProjects);
        }
    }

    @PostMapping("/upload/image/{projectId}")
    public ResponseEntity<String> uploadProjectImage(@PathVariable Long projectId,@RequestParam("file") MultipartFile file) throws FileUploadException  {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // we reuse the securitycontext since
        //only auth users can upload image to their projects.
        UserEntity user = userRepository.findByEmail(email).get(); //so i fetch the user and find it by email where this checks for auth,

        //and then store this in a Long datatype with the reference userId and point this to the fetched user.getUserId();
        //userId has the actual userId, this could have been fixed in another way, maybe just pointing to the email instead, since it is unique
        //and the email could have several projects, and the authenticationManager points to the email for auth credentials.
        Long userId = user.getUserId();
            String result = projectService.uploadProjectImage(projectId,userId,file);
            return ResponseEntity.status(201).body("Image uploaded successfully with filename: " + result);
    }

    @PostMapping("upload/files/{projectId}")
    public ResponseEntity<List<String>>uploadFilesIntoProject(@PathVariable Long projectId, @RequestParam("content")List<MultipartFile> differentFiles) throws FileUploadException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> savedPaths = projectService.uploadFilesIntoProject(projectId,email,differentFiles);
        return ResponseEntity.status(201).body(savedPaths);
    }

    @GetMapping("/content/{projectId}")
    public ResponseEntity<List<ContentDTO>> getContentForProject(@PathVariable Long projectId) {
        List<ContentDTO> content = projectService.getContentForProject(projectId);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/delete/{projectId}/{contentId}")
    public ResponseEntity<String>deleteContent(@PathVariable Long projectId, @PathVariable Long contentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        projectService.deleteContent(projectId,contentId,email);
        return ResponseEntity.ok("Content deleted successfully");
    }
}
