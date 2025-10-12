package com.silvestre.web_applicationv1.enums;

public enum MimeType {
    TEXT("text/plain"),
    HTML("text/html"),
    PDF("application/pdf"),
    IMAGE("image/*"),       // generic for any image
    VIDEO("video/*"),       // generic for any video
    AUDIO("audio/*"),       // optional if you want voice memos
    FILE("application/octet-stream");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
