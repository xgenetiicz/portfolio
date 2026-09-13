package com.example.genetiicz.Repository;

import com.example.genetiicz.Entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /*
    So the method is written as a List of course, since i want to fetch all the projects
    made by a user -> in this case admin. But there will be changes later when scaling,
    there is a plan for that.

    So I want to find ALL PROJECTS, by userId since I have ManyToOne and a JoinColumn on user_id.
    So i want jpa to find the UserId that is requested, but also filter it on the specific email that
    the userId is assigned to. Because each EMAIL is = unique. So one email can be part of a project.
     */
    List<ProjectEntity> findAllByUserEntity_Email(String email);

    /*
    Change of plans, I want to show userName instead of showcasing other's email in the url where this is sensitive information.
    It does not make sense to leave it open there of course. lol.
     */
    List<ProjectEntity> findAllByUserEntity_UserName(String userName);

    /*
    So if the user have or does not have an project, there should be an optional value of it so the image that represents the value
    of the page should be an object or null.
     */


    // it is sat as AndUserEntity_UserId since i am telling jpa hibernate
    // to find the userId at UserEntity table. projectId already has a joincolumn on it manytoOne, but the declarative query still needs to be declared where to fetch it.
    Optional <ProjectEntity>findProjectByProjectIdAndUserEntity_UserId(Long projectId,Long userId);
}
