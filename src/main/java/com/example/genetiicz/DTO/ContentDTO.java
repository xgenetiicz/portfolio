package com.example.genetiicz.DTO;

import com.example.genetiicz.Enum.ContentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ContentDTO {
    private Long contentId;
    private String filePath;
    private long fileSize;
    private ContentType contentType;
}
