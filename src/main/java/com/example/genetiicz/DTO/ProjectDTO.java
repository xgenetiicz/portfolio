package com.example.genetiicz.DTO;

import com.example.genetiicz.Enum.ProjectCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
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
    //removed notBlank on projectURL - beacuse it can be blank now.
    private String projectURL;

    @NotNull(message = "*Date is required*")
    private LocalDate startDate;

    //No annotation - because a project could be also current project.
    private LocalDate endDate;

    private String imagePath;

    //List for retrieving all contents within the project.
    private List<ContentDTO> content;

    @JsonProperty("isActive")
    private boolean isActive; //It should be false by standard.

    @JsonProperty("isFeatured")
    private boolean isFeatured;

    //it should fetch the keywords referred to the projectId-or put them there.
    private List<String> keywords;


    @AssertTrue(message = "End date cannot be before start date")
    public boolean isEndDate() {
        return endDate == null || !endDate.isBefore(startDate);
    }

    private ProjectCategory projectCategory;
}
