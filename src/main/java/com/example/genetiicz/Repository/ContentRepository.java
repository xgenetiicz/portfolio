package com.example.genetiicz.Repository;

import com.example.genetiicz.Entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    /*
    We list and find All files by ProjectEntity and join the column on projectId.
    It is referred as:

    @ManyToOne
    @JoinColumn(name = "project_id") //so this table should have many to one joins on projectId
    private ProjectEntity projectEntity;

     */
    List<ContentEntity> findAllByProjectEntity_ProjectId(Long projectId);

    //If not By - you will get an exception of findContentId as a field instead of ContentId
    Optional<ContentEntity> findByContentIdAndProjectEntity_ProjectId(Long contentId, Long projectId);
}
