package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserShowingRoleDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private Role role;
    private boolean enabled;
    private LocalDate dateAdded;
    private boolean emailVerified;

    public UserShowingRoleDto(User user){
        this.id = user.getId();
        this.firstName= user.getFirstname();
        this.lastName= user.getLastname();
        this.email= user.getEmail();
        this.role= user.getRole();
        this.avatarUrl =user.getAvatarUrl();
        this.enabled =  user.isEnabled();
        this.dateAdded = user.getDateAdded().toLocalDate();
        this.emailVerified = user.isVerifiedEmail();
    }
//    {
//        id: 3,
//                firstName: "Carol",
//            lastName: "Davis",
//            email: "carol@example.com",
//            avatarUrl: "https://i.pravatar.cc/150?img=5",
//            role: "PLANNER",
//    }
}
