package com.silvestre.web_applicationv1.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailMessage {
    private String sender;
    private String receiver;
    private String body;
    private String subject;
}
