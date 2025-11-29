package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.StaffRole;
import com.silvestre.web_applicationv1.repository.StaffRoleRepo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("public/staff-roles")
public class StaffRoleController {

    @Autowired
    private StaffRoleRepo staffRoleRepo;


    @GetMapping
    public ResponseEntity<?> getAllStaffRoles(){
        List<StaffRole> staffRoles= staffRoleRepo.findAll();
        return ResponseEntity.ok(staffRoles);
    }


    @GetMapping("/{id}")
    public ResponseEntity<StaffRole> getStaffRoleById(@PathVariable Long id) {
        Optional<StaffRole> staffRole = staffRoleRepo.findById(id);
        return staffRole.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StaffRole> createStaffRole(@RequestBody StaffRoleRequest staffRoleRequest) {
        try {

            StaffRole toCreate= new StaffRole();
            toCreate.setRoleName(staffRoleRequest.getRoleName());
            toCreate.setRoleDescription(staffRoleRequest.getRoleDescription());


            StaffRole savedRole = staffRoleRepo.save(toCreate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffRole> updateStaffRole(
            @PathVariable Long id,
            @RequestBody StaffRoleRequest staffRoleDetails) {

        return staffRoleRepo.findById(id)
                .map(existingRole -> {
                    existingRole.setRoleName(staffRoleDetails.getRoleName());
                    existingRole.setRoleDescription(staffRoleDetails.getRoleDescription());
                    StaffRole updatedRole = staffRoleRepo.save(existingRole);
                    return ResponseEntity.ok(updatedRole);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Getter
    @Setter
    public static class StaffRoleRequest {
        @NotBlank(message = "Role name is required")
        private String roleName;

        private String roleDescription;

        // getters, setters
    }

}
