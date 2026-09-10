package com.example.genetiicz.ProjectTest;

import com.example.genetiicz.Entity.ProjectEntity;
import com.example.genetiicz.Repository.ProjectRepository;
import com.example.genetiicz.Service.ProjectService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class imageFile {


    //I want to mock this because it is here the images will be stored.
    //And i also don't need to use a userId here since i have an ManyToOne and join Column on userId
    @Mock
    private ProjectRepository projectRepository;

    //InjectMocks are where i inject all the mocks required for usage.
    //This is actually the constructor initializing the literal values
    @InjectMocks
    private ProjectService projectService; //And the logic will go to projectService.

    /*
    So this test is going to be a TDD - Test Driven Development.
    The idea is to get this to pass on green before i decide to implement anything
    I think this is a great idea too se actually how i can implement the feature, and
    what i actually need in terms of building bloks.
     */

    @Test
    void imageUpload_ShouldWork_WithProjectId() throws FileUploadException { //So i don't store userId since there is already annotated @ManyToOne
    // Arrange the test
        ProjectEntity projectImage = new ProjectEntity();
        projectImage.setImagePath("uploads/projects/123/test-image.jpg"); //Just getting the actually image that is being set

        //Need to mock a file now:
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        // I want to check for when the projectRepo is finding projectId, then it should check an optional maybe
        //if there is a photo from before?
        when(projectRepository.findProjectByProjectIdAndUserEntity_UserId(123L,1L))
                .thenReturn(Optional.of(projectImage)); //The Id's are stored as LONG

        //So when the projectRepo finds it, we want to store the result in a String result = the actual service layer - business logic

        String result = projectService.uploadProjectImage(123L,1L,mockFile);
        assertTrue(result.startsWith("uploads/projects/123"));

        System.out.println(result);
    }
}
