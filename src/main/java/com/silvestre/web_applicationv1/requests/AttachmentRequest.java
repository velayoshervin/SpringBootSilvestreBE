package com.silvestre.web_applicationv1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequest {
    private String filename;
    private String url;
    private Long fileSize;
}
