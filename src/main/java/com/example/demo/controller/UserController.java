package com.example.demo.controller;

import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.User;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("register")
    public User register(@RequestBody User user) {
        return service.register(user);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        return ResponseEntity.ok(service.verify(user));
    }

    @PostMapping("verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto request) {
        try{
            service.verifyUser(request);
            return ResponseEntity.ok("User verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
