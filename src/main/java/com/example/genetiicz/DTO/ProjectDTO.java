package com.example.genetiicz.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

public class ProjectDTO {

    //DTO holder datafields, og vi ønsker å ha:
    // projectName, projectDescription osv.

    private Long projectId;

    @NotBlank(message = "*Project Name/Title is required*")
    private String projectName; //Title

    @NotBlank(message = "*Description is required*")
    private String projectDescription; //Description for the project

    //need to set the value for the projects URL so it is accessable to others later on project view
    //
    @NotBlank(message = "*URL is required*")
    private String projectURL;

    @NotNull(message = "*Date is required*")
    private LocalDate startDate;

    //No annotation - because a project could be also current project.
    private LocalDate endDate;

    private String imagePath;

    //List for retrieving all contents within the project.
    private List<ContentDTO> content;



    //This is for putting image to the project, so this will reveal as a background image
    //on the cards. The idea is to have projectCards that retrieves this image and set it on the
    //project card but this needs to be converted to bytes in db.


    //THE BEST WAY TO DO THIS IS TO MAKE THIS WORK AS AN METHOD public void saveFile() need to look on this later
    //-will do this another time.
   // private MultipartFile projectFile;

}
