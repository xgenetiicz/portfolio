package com.example.genetiicz.Entity;


import com.example.genetiicz.Enum.ContentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "content_records")
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column
    private String filePath; //and the bytes should point to the filepath (String)

    /**
     * With multipartFile, I learned that the instead of storing each byte, where the range of a byte
     * in Java is from -128-127, so a photo of 200KB is a where one 1KB = 1024 bytes. So a 200KB
     * surpasses the byte range greatly, where this will not function right because of the file
     * So the multipartFile will store(convert) the bytes into the primitive datatype of long instead
     * a long datatype stores up to 64-bit - where 8 bits represent one byte.
     * so private long fileSize stores the total count of all bytes (64-bits long)
     *
     */
    @Column
    private long fileSize;

    @Column
    @CreationTimestamp
    private LocalDateTime uploadDate; //okay to have timestamps for each file uploaded

    @ManyToOne
    @JoinColumn(name = "project_id") //so this table should have many to one joins on projectId
    private ProjectEntity projectEntity;
    /*
    So several files is associated with the projectId.
     */

    //ContentType is declared with an enumirated list - so for each content the path is specified.
    @Enumerated(EnumType.STRING)
    @Column
    private ContentType contentType;
}
