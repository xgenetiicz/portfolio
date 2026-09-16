package com.example.genetiicz.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

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

    @Column
    private String projectDescription;

    @Column(unique = true) //one url for one project, can have several also - but each url must be unique.
    private String projectURL;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
