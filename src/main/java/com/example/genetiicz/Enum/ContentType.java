package com.example.genetiicz.Enum;

import com.fasterxml.jackson.annotation.JsonValue;
public enum ContentType {

    //Images
    JPEG("image/jpeg"),
    PNG("image/png"),
    HEIC("image/heic"),
    WEBP("image/webp"),
    GIF("image/gif"),

    // Video
    MP4("video/mp4"),
    MOV("video/quicktime"),
    WEBM("video/webm"),

    // Documents
    //pdf is only thing I should accept, if not - i can change it here and add extra.
    //Should Accept ZIP also
    PDF("application/pdf"),
    ZIP ("application/zip"),

    //okay found out that windows browser process zip files different??? and the first one is already used by macOS and Linux
    ZIP_WINDOWS ("application/x-zip-compressed");

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    //When jackson serialize an ContentType of Enum - instead of looking at .name()
    //It should use the return value of method instead as video/mp4
    @JsonValue
    public String getMimeType() {
        return mimeType;
    }
}
