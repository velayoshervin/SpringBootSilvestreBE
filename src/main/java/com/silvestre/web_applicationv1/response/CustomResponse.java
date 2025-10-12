package com.silvestre.web_applicationv1.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponse <T>{
    private T data;
    private LocalDateTime sent;
    private String status;
}
