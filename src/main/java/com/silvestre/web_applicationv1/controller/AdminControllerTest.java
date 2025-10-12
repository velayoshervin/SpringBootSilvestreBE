package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.ExceptionHandler.EmailExistsException;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.AddUserRequest;
import com.silvestre.web_applicationv1.requests.SignInRequest;
import com.silvestre.web_applicationv1.service.PasswordGeneratorService;
import com.silvestre.web_applicationv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@PreAuthorize("hasRole('ADMIN')")
@RestController()
public class AdminControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordGeneratorService passwordGeneratorService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/admin/test")

    public ResponseEntity<String> adminOnlyEndpoint() {
        return ResponseEntity.ok("✅ Access granted: You are an ADMIN!");
    }

    @PostMapping("/admin-addUser")
    public ResponseEntity<?> createPlanner(@RequestBody AddUserRequest request){

        String rawPassword= passwordGeneratorService.generateRawPassword(16);

        String email = request.getEmail();
        Role role = request.getRole();

        if(userRepository.findByEmail(email).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("email already exists");


        User user = User.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.PLANNER)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        User saved= userRepository.save(user);

        Map<String,String> response = Map.of("firstname",request.getFirstname(),
                "lastname", request.getLastname(),"email", request.getEmail(),
                 "access" , request.getRole().toString(),
                "password", rawPassword);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/admin-enableAccount")
    public ResponseEntity<?> toggleUserEnable(@RequestParam Long userId, @RequestParam boolean value) {

        System.out.println(userId + " username and value " + value);
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user not found"));
        user.setEnabled(value);
        userRepository.save(user);
        String message = value? "account has been enabled" : "account has been disabled";
        return ResponseEntity.ok(message);
    }
}
