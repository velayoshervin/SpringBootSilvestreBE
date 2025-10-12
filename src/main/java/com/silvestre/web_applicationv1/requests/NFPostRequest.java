package com.silvestre.web_applicationv1.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFPostRequest {

    private String content;
    private Long userId; // the author of the post
    private List<MediaDTO> mediaList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class MediaDTO {
        private String url;
        private String type; // IMAGE or VIDEO
    }
}
