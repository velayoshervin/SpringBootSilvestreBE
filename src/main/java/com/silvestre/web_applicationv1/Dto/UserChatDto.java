package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserChatDto {

    private Long userId;
    private String avatarUrl;
    private String firstname;
    private String lastname;

    public UserChatDto(User user){
        this.userId = user.getId();
        this.avatarUrl= user.getAvatarUrl();
        this.firstname= user.getFirstname();
        this.lastname= user.getLastname();
    }
}
