package com.silvestre.web_applicationv1.requests;

import com.silvestre.web_applicationv1.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicInfoRequest {

    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String address;
    private boolean enableSmsOtp;
    private boolean enableEmailOtp;
    private boolean updatingPassword;
    private String password;
    private String updatedPassword;

    //enableEmailOtp: true
    //enableSmsOtp: true

}
