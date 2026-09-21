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

import java.io.FileNotFoundException;
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
        String relativePath = "uploads/projects/" + projectId + "/cover/" + filename; // the photo shall be stored in dir "cover"

        //Then i need to store those files physically
        Path uploadPath = Paths.get("uploads/projects/" + projectId +"/cover/");
        try {
            Files.createDirectories(uploadPath); //making dir for the actualpath where the files should be copied too.
            Files.copy(imageUrlProject.getInputStream(),Paths.get(relativePath));
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
            project.setImagePath(relativePath); //filename contains UUID.randomUUID() + "_" imageUrlProject.getOriginalFileName
            projectRepository.save(project);
            return  relativePath; // we save the dir here.
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


    //Method for deleting Image cover on project
    public void deleteProjectImage(Long projectId, String email) {
        Optional<UserEntity> seededAdmin = userRepository.findByRoleAndEmail(Role.ADMIN, email);
        if (seededAdmin.isEmpty()) {
            throw new NotAuthorizedException("Not authorized for this request");
        }

        Optional<ProjectEntity> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }

        ProjectEntity currentProject = project.get();
        String imagePath = currentProject.getImagePath();

        if (imagePath == null) {
            throw new ServerResourceException("This project has no image to delete");
        }

        Path filePath = Paths.get(imagePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        currentProject.setImagePath(null);
        projectRepository.save(currentProject); //save the currentProject without imagePath after deletion.
    }

    //Download files - the idea is that the user have possibility to download the content within the project.
    //But these files should only be associated with pdf for now. JPEG/PNG and etc with MP4 should be viewed.

    //only PDF & ZIP should be allowed to download.

    public String downloadContent(Long projectId,Long contentId) throws FileUploadException, FileNotFoundException {

        Optional<ProjectEntity> findProject = projectRepository.findById(projectId);
        if(findProject.isEmpty()) {
            throw new ProjectNotFoundException("Project not found");
        }
            Optional<ContentEntity> findContent = contentRepository.findById(contentId);
            if (findContent.isEmpty()){
                throw new FileNotFoundException("Content is not found for this project");
            }
                //If we find the contentId, store the object into contentAvailable so i can crosscheck with Enum list -> and can use primitive statement checks instead of .equal()
        ContentEntity contentAvailable = getContentEntity(projectId, findContent);

        return  contentAvailable.getFilePath();
    }

    private static ContentEntity getContentEntity(Long projectId, Optional<ContentEntity> findContent) throws FileNotFoundException, FileUploadException {
        //Nice ->IntelliJ extracted the method and made projectId and findContent return ContentAvailable.
        ContentEntity contentAvailable = findContent.get();

        //Now, i need to crosscheck that the content is referred to the projectId.

        if (!contentAvailable.getProjectEntity().getProjectId().equals(projectId)) {
            throw new FileNotFoundException("Content is not found for this project");
        }

        //crosscheck here with operators
        if(contentAvailable.getContentType() != ContentType.PDF && contentAvailable.getContentType() != ContentType.ZIP) {
            throw  new FileUploadException("Only ZIP files can be downloaded this way");
        }
        return contentAvailable;
    }
}
