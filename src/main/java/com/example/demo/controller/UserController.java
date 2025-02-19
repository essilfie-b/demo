package com.example.demo.controller;

import com.example.demo.model.AccessKey;
import com.example.demo.model.User;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService service;
    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterUserRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Assuming service.register(request) returns a success message or object
            var result = service.register(request);
            response.put("success", true);
            response.put("data", result); // Include any relevant data returned from registration
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("all-users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(service.getUsers());
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("verify")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestBody VerifyUserRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            service.verifyUser(request);
            response.put("success", true);
            response.put("message", "User verified successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
            return ResponseEntity.ok(token);

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
