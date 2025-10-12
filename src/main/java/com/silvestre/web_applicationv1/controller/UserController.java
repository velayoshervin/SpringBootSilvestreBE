package com.silvestre.web_applicationv1.controller;
import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.Dto.UserShowingRoleDto;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.Role;
import com.silvestre.web_applicationv1.filter.JwtUtil;
import com.silvestre.web_applicationv1.properties.JwtProperties;
import com.silvestre.web_applicationv1.requests.BasicInfoRequest;
import com.silvestre.web_applicationv1.requests.SignInRequest;
import com.silvestre.web_applicationv1.requests.UpdateRoleRequest;
import com.silvestre.web_applicationv1.response.CustomResponse;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.response.UserResponse;
import com.silvestre.web_applicationv1.service.EmailTokenService;
import com.silvestre.web_applicationv1.service.MediaService;
import com.silvestre.web_applicationv1.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/public/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private EmailTokenService emailTokenService;
    @Autowired
    private MediaService mediaService;

    @PostMapping
    public ResponseEntity<?> handleFormCreateAccount(@RequestBody SignInRequest signInRequest){

        //This handles form login ( is verified is false by default for form login
        Role defaultRole = Role.CUSTOMER;
        User user= userService.createUser(signInRequest,defaultRole);

        UserDto userDto= UserDto.builder().
                userId(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .isEmailVerified(user.isVerifiedEmail())
                .role(user.getRole().name())
                .build();


        CustomResponse<UserDto> response = CustomResponse.<UserDto>builder()
                .data(userDto)
                .status("success")
                .sent(LocalDateTime.now())
                .build();

        //where I need to send the verificationLink
        emailTokenService.sendVerificationToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleFormLogin(@RequestBody Map<String,String> requestBody, HttpServletResponse response){
        String email= requestBody.get("email");
        String rawPassword= requestBody.get("password");

        System.out.println(email + " " + rawPassword);

        User user = userService.findByEmail(email);
        try{    
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,rawPassword
                    ));

        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect username or password");
        }

        if(!user.isVerifiedEmail()){
            emailTokenService.sendVerificationToken(user);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","user needs to verify email account",
                    "userId", user.getId()));
        }
        String jwt= jwtUtil.generateToken(user);


        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false) // set to false if you're not using HTTPS locally /Set this to false only during local development if you're not using HTTPS.
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getExpirationMs()))
                .sameSite("Lax") // or "Lax" -- none . check later
                .build();

        ;
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping()
    public PaginatedResponse<UserResponse> findUserPage(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userService.findUserPage(pageNumber,pageSize);
    }

    @PutMapping("update-role")
    public ResponseEntity<?> updateRole(@RequestBody UpdateRoleRequest updateRoleRequest){
        userService.UpdateRole(updateRoleRequest.getId(), updateRoleRequest.getRole());
        return ResponseEntity.ok("user updated");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("update-userInfoSettings")
    public ResponseEntity<?> updateBasicInfo(@RequestBody BasicInfoRequest basicInfoRequest, @RequestParam Long userId) {
      return userService.updateUserInfo(userId,basicInfoRequest );
    }

    @GetMapping("/userAccounts")
    public PaginatedResponse<UserShowingRoleDto> findUserAccounts( @RequestParam(required = false) Role filter,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        int pageNum = pageNumber;
    // 0 based from 1 based pagination frontend
        if(pageNum > 0)
            pageNum = pageNum -      1;

        Pageable pageable = PageRequest.of(pageNum, pageSize);

        Page<User> users=userService.getUserAccounts(pageable,filter);

       Page<UserShowingRoleDto> page= users.map((UserShowingRoleDto::new));

       return new PaginatedResponse<>(page);
    }

    @GetMapping("/user-contacts")
    public ResponseEntity<?> getUserContacts()
    {
     List<UserShowingRoleDto>  users=   userService.getUserContacts();
     return ResponseEntity.ok(users);
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam (name = "file") MultipartFile file, @RequestParam Long userId ){

        System.out.println(file + "file object");
        System.out.println(userId +"userId");

        User user= userService.findUserById(userId);
        UserResponse userResponse = new UserResponse();
        if(file.isEmpty() && !user.getAvatarPublicId().trim().isEmpty()){
            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
            User saved= userService.save(user);
            userResponse= new UserResponse(saved);
        }
        else {
          userResponse= mediaService.uploadAvatar(file,userId);
        }
        return ResponseEntity.ok(userResponse);
    }
    

}
