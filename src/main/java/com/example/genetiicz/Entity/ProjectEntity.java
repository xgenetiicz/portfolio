package com.example.genetiicz.Entity;

import com.example.genetiicz.Enum.ProjectCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter

@Entity
@Table(name = "project_records")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // we want hibernate to actually count automatically on postgreSQL
    private Long projectId;

    @Column
    private String projectName;

    //varchar on 255 limit
    @Column(length = 2000)
    private String projectDescription;

    @Column(unique = true) //one url for one project, can have several also - but each url must be unique.
    private String projectURL;

    @ElementCollection
    @CollectionTable(
            name = "keywords_records", //name of table and it should have the collection of joining columns at
            joinColumns = @JoinColumn(name = "project_id") // the foreign key to projectId

    )
    @Column(name ="keywords")
    private List<String> keywords;

    @Column // i need a column to say explicitly if the project is active or inactive.
    private boolean isActive;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String imagePath;

    //for homepage if the project should be displayed there. if true it should be there.
    @Column
    private boolean isFeatured;

    @Enumerated(EnumType.STRING)
    @Column
    private ProjectCategory projectCategory;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
