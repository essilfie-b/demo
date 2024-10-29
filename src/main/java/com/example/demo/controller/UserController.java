package com.example.demo.controller;

import com.example.demo.request.LoginUserRequest;
import com.example.demo.request.RegisterUserRequest;
import com.example.demo.request.ResetPasswordRequest;
import com.example.demo.request.VerifyUserRequest;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserRequest request) {
        try{
            service.verifyUser(request);
            return ResponseEntity.ok("User verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam String email) {
        service.resetPasswordRequest(email);
        return ResponseEntity.ok("Password reset email sent.");
    }

    @GetMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        if (service.isTokenValid(token))
            return ResponseEntity.ok("Token is valid, please enter your new password.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        try {
            service.resetPassword(token, request);
            return ResponseEntity.ok("Password has been reset successfully.");
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("resend-code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            service.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent successfully");
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
