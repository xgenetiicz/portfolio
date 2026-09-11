package com.example.genetiicz.Enum;

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
    PDF("application/pdf");

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
