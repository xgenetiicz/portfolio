package com.example.genetiicz.Service;

import com.example.genetiicz.DTO.ContentDTO;
import com.example.genetiicz.Entity.ContentEntity;
import com.example.genetiicz.Entity.ProjectEntity;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.ContentType;
import com.example.genetiicz.Enum.Role;
import com.example.genetiicz.Exceptions.NotAuthorizedException;
import com.example.genetiicz.Exceptions.ProjectNotFoundException;
import com.example.genetiicz.Exceptions.ServerResourceException;
import com.example.genetiicz.Repository.ContentRepository;
import com.example.genetiicz.Repository.ProjectRepository;
import com.example.genetiicz.Repository.UserRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class ContentService {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private ContentRepository contentRepository;

    //THESE ARE FOR UPLOADS AND SHOULD BE STATIC THROUGHOUT THE CLASS
    private static final long MAX_TOTAL_BYTES = 50L * 1024 * 1024; //equals 50MB.

    public ContentService(ContentRepository contentRepository,
                          ProjectRepository projectRepository,
                          UserRepository userRepository
    ){
        this.contentRepository = contentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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

    //Upload files into project method
    public List<String> uploadFilesIntoProject(Long projectId, String email, List<MultipartFile> differentFiles) throws FileUploadException {
        Optional <ProjectEntity> project = projectRepository.findById(projectId);
        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);

        if(seededAdmin.isEmpty()) {
            throw new NotAuthorizedException("You are not Authorized to do this"); //Again defense in depth - there should be no one else logged into this than me
        }

        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        //THIS REPRESENTS THE CURRENT PROJECT
        ProjectEntity currentProject = project.get();

        List<ContentEntity> existingContent = contentRepository.findAllByProjectEntity_ProjectId(projectId);
        long currentTotalBytes = existingContent.stream().mapToLong(content-> content.getFileSize()).sum();

        List<String>savedPaths = new ArrayList<>();

        for (MultipartFile files : differentFiles) {
            long newFileSize = files.getSize(); //for each differentfiles we store them into files, and then check the newFileSize and the currentTotalBytes

            // MAX_TOTAL_BYTES IS A PRIVATE FINAL LONG THAT GOES THROUGH THE WHOLE METHOD
            if(newFileSize + currentTotalBytes > MAX_TOTAL_BYTES) {
                throw new FileUploadException("Upload would exceed the 50MB total limit for this project");
            }

            //This checks with the ContentEnum type list.
            String mimeType = files.getContentType();
            ContentType contentType = Arrays.stream(ContentType.values())
                    .filter(ct -> ct.getMimeType().equals(mimeType)).findFirst()
                    .orElseThrow(() -> new FileUploadException("Unsupported file type: " + mimeType));


            //I am reusing the save path from method uploadProjectImage()
            //So i generate first random unique filenames
            String fileName = UUID.randomUUID() + "_" + files.getOriginalFilename();
            //Creating a Path for storage and store this into uploadFiles
            Path uploadFiles = Paths.get("uploads/projects/" + projectId + "/content/");
            try {
                Files.createDirectories(uploadFiles); //making dir for the actual path where the files should be copied too.
                Files.copy(files.getInputStream(),uploadFiles.resolve(fileName)); // the for enhanced of the elements are stored into **files**
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Creating the new instance of ContentEntity and saving the files
            ContentEntity content = new ContentEntity();
            content.setFilePath("uploads/projects/" + projectId + "/content/" + fileName);
            content.setFileSize(newFileSize);
            content.setContentType(contentType);
            content.setProjectEntity(currentProject);
            contentRepository.save(content);

            currentTotalBytes = currentTotalBytes + newFileSize;
            savedPaths.add(content.getFilePath());
        }
        return savedPaths;
    }

    //getContentForProject Method

    public List<ContentDTO> getContentForProject (Long projectId){
        //Optional
        Optional <ProjectEntity> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        List <ContentEntity> existingContent = contentRepository.findAllByProjectEntity_ProjectId(projectId);

        //we retrieve a list on the contentDTO store this in the heap memory with an reference object
        // of contentDTOS with a new ArrayList. We want the list
        List <ContentDTO> contentDTOS = new ArrayList<>();

        for (ContentEntity content : existingContent) {
            ContentDTO dtoForProject  = new ContentDTO();

            dtoForProject.setContentId(content.getContentId());
            dtoForProject.setFilePath(content.getFilePath());
            dtoForProject.setFileSize(content.getFileSize());
            dtoForProject.setContentType(content.getContentType());
            contentDTOS.add(dtoForProject);
        }

        return contentDTOS;
    }

    //Delete content on Project method.
    public void deleteContent(Long projectId,Long contentId,String email) {

        /*
        I needed to create the object project, and have a declarative method on findById(projectId).
        The reason is i want to have a pointer on it if admin exists as it should do - it would grant the ability
        to delete the content within the project by pointing it to contentId.

        so if admin = true -> project = true -> deletebyId on contentId= true -- since ContentEntity has @ManyToOne
        Join on column @JoinColum(name =  "project_id");
         */

        System.out.println("deleteContent() is being called on");

        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN,email);
        Optional<ContentEntity>existingContent = contentRepository.findByContentIdAndProjectEntity_ProjectId(contentId,projectId);
        if (seededAdmin.isPresent()) {

            //Debugging
            UserEntity admin = seededAdmin.get();
            System.out.println(admin.getFirstName() + " " + admin.getLastName());

            if( existingContent.isPresent()){
                List<ContentDTO> contentName = getContentForProject(projectId);
                //Debugging
                System.out.println(contentName + " " + "are available");

                //got help on this one - I need to also delete this on server level.
                ContentEntity contentOnServer = existingContent.get(); //so i store the object into contentOnServer

                Path filePath = Paths.get(contentOnServer.getFilePath()); //this will find the actual uri path on server lever
                //but also after the actual content that is referred to the projectId with their contentId.
                try{
                    Files.deleteIfExists(filePath); //and then we try to delete the files with a inbuild class such as Files, with the method deleteIfExists(my actual file (filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                contentRepository.deleteById(contentId); //delete the specific content on the project
            } else {
                throw new ServerResourceException("Check server: ");
            }
        } else {
            throw new NotAuthorizedException("Not authorized for this requests "); // this should never appear, but we leave it here.
        }
        //don't return anything this is a void method
    }
}
