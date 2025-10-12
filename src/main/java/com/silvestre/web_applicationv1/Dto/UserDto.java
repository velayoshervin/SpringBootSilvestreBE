package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long userId;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private boolean isEmailVerified;
    private String role;
    private String avatarUrl;



    public UserDto(User user){
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.isEmailVerified= user.isVerifiedEmail();
        this.userId= user.getId();
        this.avatarUrl= user.getAvatarUrl();
    }

}
