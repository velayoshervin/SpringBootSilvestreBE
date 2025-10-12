package com.silvestre.web_applicationv1.service;


import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.ExceptionHandler.EmailExistsException;
import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.BasicInfoRequest;
import com.silvestre.web_applicationv1.requests.SignInRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.response.UserContactResponse;
import com.silvestre.web_applicationv1.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User createUser(SignInRequest signInRequest,Role role){

       Optional<User> user= userRepository.findByEmail(signInRequest.getEmail());
       if(user.isPresent())
           throw new EmailExistsException("email already in used");
        //create new user
        String password = passwordEncoder.encode(signInRequest.getPassword());
        User user1 = User.builder()
                .email(signInRequest.getEmail())
                .firstname(signInRequest.getFirstname())
                .lastname(signInRequest.getLastname())
                .password(password)
                .role(role)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        return userRepository.save(user1);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user doesn't exists"));
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("email is not tagged to any account. user not found"));
    }


    public PaginatedResponse<UserResponse> findUserPage(int pageNumber, int pageSize) {

        Pageable pageable= PageRequest.of(pageNumber,pageSize);
        Page<UserResponse> page =userRepository.findAll(pageable).map(UserResponse::new);

        return new PaginatedResponse<UserResponse>(page);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void UpdateRole(Long userId, Role role) {
        User existing = findUserById(userId);
        existing.setRole(role);
        userRepository.save(existing);
    }


    public ResponseEntity<?> updateUserInfo(Long userId, BasicInfoRequest basicInfoRequest) {

        System.out.println("updated password "+basicInfoRequest.getUpdatedPassword());
        System.out.println(basicInfoRequest);

        User existing = findUserById(userId);

        //validate user if updating password
        if (basicInfoRequest.isUpdatingPassword()) {
            String rawPassword = basicInfoRequest.getUpdatedPassword();

            // Validate that new password is not empty
            if (rawPassword == null || rawPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Provide valid new password");
            }

            // Validate current password
            if (!validatePassword(existing.getEmail(), basicInfoRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Current password is incorrect");
            }

            // If all validations pass, update password
            existing.setPassword(passwordEncoder.encode(rawPassword));
        }
        existing.setFirstname(basicInfoRequest.getFirstname());
        existing.setLastname(basicInfoRequest.getLastname());
        existing.setEmail(basicInfoRequest.getEmail());
        existing.setPhone(basicInfoRequest.getPhone());
        existing.setEnableEmailOtp(basicInfoRequest.isEnableEmailOtp());
        existing.setEnableSmsOtp(basicInfoRequest.isEnableSmsOtp());

        //enableEmailOtp: true
        //enableSmsOtp: true

        userRepository.save(existing);

        return ResponseEntity.ok("password saved");
    }

    private boolean validatePassword(String email, String oldPassword){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,oldPassword
                    ));

        return true;
        }catch (BadCredentialsException e){
            return false;
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getUserAccounts(Pageable pageable,Role roleFilter){

        if(roleFilter == null)
            return userRepository.findAll(pageable);
        return userRepository.findByRole(roleFilter,pageable);
    }

    public List<UserShowingRoleDto> getUserContacts() {
        return userRepository.findAll().stream().map(UserShowingRoleDto::new).toList();
    }
}
