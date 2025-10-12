package com.silvestre.web_applicationv1.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.silvestre.web_applicationv1.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "user_id")
    private Long id;

    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String firstname;
    private String lastname;
    private String address;
    private String phone;
    @JsonIgnore
    private boolean verifiedEmail;
    private String avatarUrl;
    private String avatarPublicId;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime dateAdded;

    //OTP

    private boolean enableSmsOtp;

    private boolean enableEmailOtp;
    //enableEmailOtp: true
    //enableSmsOtp: true


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    private boolean enabled;
    @JsonIgnore
    private boolean accountNonLocked = true;
    @JsonIgnore
    private boolean accountNonExpired = true;
    @JsonIgnore
    private boolean credentialsNonExpired = true;

    @PrePersist
    protected void onCreate() {
        this.dateAdded = LocalDateTime.now();
    }

}
