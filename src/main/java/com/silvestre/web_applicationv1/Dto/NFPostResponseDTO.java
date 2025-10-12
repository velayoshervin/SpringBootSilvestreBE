package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.NFPost;
import com.silvestre.web_applicationv1.entity.User;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NFPostResponseDTO {

    private Long id;
    private String content;
    private AuthorDTO author;
    private List<MediaDTO> mediaList;
    private int likeCount;
    private int commentCount; // optional, can be 0 if not loaded
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class AuthorDTO {
        private Long id;
        private String username;
        private String avatarUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MediaDTO {
        private String url;
        private String type; // IMAGE or VIDEO
    }

    // ✅ Convenience constructor from NFPost entity
    public NFPostResponseDTO(NFPost post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt(); // from Auditable

        // Map author
        User user = post.getUser();
        if (user != null) {
            this.author = AuthorDTO.builder()
                    .id(user.getId())
                    .username(user.getFirstname() + " " + user.getLastname())
                    .avatarUrl(user.getAvatarUrl())
                    .build();
        }

        // Map media list
        if (post.getMediaList() != null) {
            this.mediaList = post.getMediaList().stream()
                    .map(m -> MediaDTO.builder()
                            .url(m.getUrl())
                            .type(m.getType().name())
                            .build())
                    .collect(Collectors.toList());
        }

        // Count likes
        this.likeCount = post.getLikes() != null ? post.getLikes().size() : 0;

        // You can calculate commentCount here or leave it 0 for now
        this.commentCount = 0;
    }
}