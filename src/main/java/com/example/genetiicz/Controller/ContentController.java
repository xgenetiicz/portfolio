package com.example.genetiicz.Controller;


import com.example.genetiicz.DTO.ContentDTO;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Repository.UserRepository;
import com.example.genetiicz.Service.ContentService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    //private ProjectService projectService;
    private ContentService contentService;
    private UserRepository userRepository;

    public ContentController(ContentService contentService,
                             UserRepository userRepository) {
        this.contentService = contentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload/image/{projectId}")
    public ResponseEntity<String> uploadProjectImage(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) throws FileUploadException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // we reuse the securitycontext since
        //only auth users can upload image to their projects.
        UserEntity user = userRepository.findByEmail(email).get(); //so i fetch the user and find it by email where this checks for auth,

        //and then store this in a Long datatype with the reference userId and point this to the fetched user.getUserId();
        //userId has the actual userId, this could have been fixed in another way, maybe just pointing to the email instead, since it is unique
        //and the email could have several projects, and the authenticationManager points to the email for auth credentials.
        Long userId = user.getUserId();
        String result = contentService.uploadProjectImage(projectId,userId,file);
        return ResponseEntity.status(201).body("Image uploaded successfully with filename: " + result);
    }

    @PostMapping("upload/files/{projectId}")
    public ResponseEntity<List<String>>uploadFilesIntoProject(@PathVariable Long projectId, @RequestParam("content")List<MultipartFile> differentFiles) throws FileUploadException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> savedPaths = contentService.uploadFilesIntoProject(projectId,email,differentFiles);
        return ResponseEntity.status(201).body(savedPaths);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ContentDTO>> getContentForProject(@PathVariable Long projectId) {
        List<ContentDTO> content = contentService.getContentForProject(projectId);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/delete/{projectId}/{contentId}")
    public ResponseEntity<String>deleteContent(@PathVariable Long projectId, @PathVariable Long contentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        contentService.deleteContent(projectId,contentId,email);
        return ResponseEntity.ok("Content deleted successfully");
    }

    @DeleteMapping("/delete/image/{projectId}")
    public ResponseEntity<String> deleteProjectImage(@PathVariable Long projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        contentService.deleteProjectImage(projectId, email);
        return ResponseEntity.ok("Project image deleted successfully");
    }

    @GetMapping("/download/{projectId}/{contentId}")
    public ResponseEntity<Resource> downloadContent(@PathVariable Long projectId, @PathVariable Long contentId) throws FileNotFoundException, FileUploadException, MalformedURLException {
        String filePath = contentService.downloadContent(projectId, contentId);
        //This one was a new one I have never encountered this.
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        Resource resource = new UrlResource(path.toUri());

        String storedFileName = path.getFileName().toString();
        String fileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

        //The downloaded file is not recognized as which MIME-Type only as an ordinary All Files
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        //The PDF file will be an inline attachment were user can choose to download from their own pdf reader instead
        //And the zip file as an attachment

        ContentDisposition.Builder contentBuilder = mediaType.equals(MediaType.APPLICATION_PDF)
                ? ContentDisposition.inline() : ContentDisposition.attachment();

        ContentDisposition disposition = contentBuilder.filename(fileName, StandardCharsets.UTF_8).build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }
}
