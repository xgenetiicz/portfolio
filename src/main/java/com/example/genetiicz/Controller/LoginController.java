package com.example.genetiicz.Controller;


import com.example.genetiicz.DTO.*;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Service.AuthService;
import com.example.genetiicz.Service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.security.auth.login.AccountNotFoundException;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api/auth")
public class LoginController {


    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    //The idea is that UserService will just have CRUD operations.

    //so the registerUsers and auth is now redirected to AuthService Class.
    public LoginController (AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @PostMapping("/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody UserDTO userDTO, VerifyUserDto verifyUserDto){
        boolean registeredAdmin = authService.registerAdmin(userDTO,verifyUserDto);
        if(registeredAdmin) {
                return ResponseEntity.status(201).body("Admin Registered Successfully");
            } else {
                return ResponseEntity.status(409).body("Admin already exists");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginUserDTO loginUserDTO){
        String authenticatedUser = authService.authenticate(loginUserDTO);
        if(!authenticatedUser.isBlank()) {
            return ResponseEntity.status(201).body(authenticatedUser);
        } else {
            return ResponseEntity.status(401).body("Not authorized, please verify Email first.");
        }
    }

    //Need also a method for verifying OTP now.
    @PostMapping("/verify/otp")
    public ResponseEntity<LoginResponseDTO>checkOneTimePassword(@RequestBody OtpDTO otpDTO) throws AccountNotFoundException {
        UserEntity otpVerification = authService.checkOneTimePassword(otpDTO, otpDTO.getEmail());
        String jwtToken = jwtService.generateToken(otpVerification.getEmail()); //so the jwtToken reference variable shall have the value of the jwtService.generateToken method, wheren this has the otpVerification code and we fetch the email from it
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwtToken,jwtService.getExpirationTime());
        if (otpVerification.isEnabled()) {
            return ResponseEntity.status(201).body(loginResponseDTO);
        } else {
         return ResponseEntity.status(401).body(loginResponseDTO);
        }
    }

    /*Controller method call for reset password, and I need two actually:
    1. It should be where just a user types their email to get their otp code for changing password
    2. the second should actually call the method to implement the logic of changing password.
     */
    @PostMapping("/forgot/password")
    public ResponseEntity<String>forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        String messageToUser = authService.forgotPassword(forgotPasswordDTO.email());
        return  ResponseEntity.ok(messageToUser);
    }

    @PostMapping("/reset/password")
    public ResponseEntity<String>resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        String messageToUser = authService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(messageToUser);
    }

    /*

    CHECK THESE LINKS FOR AUTH IMPLEMENT OF JWT TOKEN:

    https://www.geeksforgeeks.org/springboot/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
    https://medium.com/@prateekjadhav8/implementing-jwt-authentication-with-spring-security-in-a-spring-boot-application-048c94ed60ba


     */


    /*@PostMapping("/login")
    public ResponseEntity Cre(@RequestBody AuthRequestDTO authRequestDTO) {
        if(userService.loadUserByUsername())

        return ResponseEntity.status(200).body("Login successfully"); //TODO: this is a template code, need to see further how i do this.
    }*/
}
