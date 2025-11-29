package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
    @Table(name = "staff_roles")
    public class StaffRole {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "staff_role_id")
        private Long staffRoleId;
        private String roleName;
        private String roleDescription;
}
