package com.example.demo.request;


import com.example.demo.model.Role;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    private String email;
    private String username;
    private String password;
    private String confirmPassword;

}
