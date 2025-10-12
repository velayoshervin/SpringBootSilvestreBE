package com.silvestre.web_applicationv1.response;

import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse{
    private Long userId;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enableSmsOtp;
    private boolean enableEmailOtp;
    private String avatarUrl;
    private String avatarPublicId;

    public UserResponse(User user){
        this.userId= user.getId();
        this.email= user.getEmail();
        this.firstname= user.getFirstname();
        this.lastname = user.getLastname();
        this.phone = user.getPhone();
        this.address= user.getAddress();
        this.role = user.getRole();
        this.enableEmailOtp= user.isEnableEmailOtp();
        this.enableSmsOtp = user.isEnableSmsOtp();
        this.avatarUrl= user.getAvatarUrl();
        this.avatarPublicId= user.getAvatarPublicId();
    }
}
