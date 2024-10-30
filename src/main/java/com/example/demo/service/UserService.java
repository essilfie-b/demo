package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.request.LoginUserRequest;
import com.example.demo.request.RegisterUserRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.ResetPasswordRequest;
import com.example.demo.request.VerifyUserRequest;
import com.example.demo.response.LoginResponse;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.example.demo.model.Role.USER;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(RegisterUserRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.info("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.info("Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.info("Password confirmation does not match for {}", request.getUsername());
            throw new RuntimeException("Password confirmation does not match");
        }

        // Create and save the new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword())); // Use request password
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(15));
        user.setAccountVerified(false);
        user.setRole(USER);

        sendVerificationEmail(user); // Send verification email
        User savedUser = userRepository.save(user); // Save user to the repository
        logger.info("Verification email sent to {}", user.getEmail());
        return savedUser;
    }


    public LoginResponse login(LoginUserRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Fetch user details from the repository
            var optionalUser = userRepository.findByUsername(request.getUsername());

            // Check if user exists and account is verified
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (!user.isAccountVerified()) {
                    logger.info("User {} attempted to log in but account is not verified.", request.getUsername());
                    return new LoginResponse(false, null, "Account not verified", null);
                }

                // Check if authentication is successful
                if (authentication.isAuthenticated()) {
                    String token = jwtService.generateToken(request.getUsername());
                    Role role = user.getRole(); // Now using Role enum

                    logger.info("User {} logged in successfully with role {}.", request.getUsername(), role.name());
                    return new LoginResponse(true, token, null, role.name());
                }
            }
        } catch (AuthenticationException e) {
            logger.warn("Failed login attempt for username: {}", request.getUsername(), e);
        }

        logger.info("Invalid login attempt for username: {}", request.getUsername());
        return new LoginResponse(false, null, "Invalid username or password", null);
    }



    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }"
                + ".container { width: 100%; max-width: 600px; margin: auto; padding: 20px; }"
                + ".header { text-align: center; padding: 20px; background-color: #007bff; color: white; border-radius: 5px 5px 0 0; }"
                + ".content { background-color: white; padding: 20px; border-radius: 0 0 5px 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                + ".verification-code { font-size: 20px; font-weight: bold; color: #007bff; text-align: center; }"
                + ".footer { text-align: center; margin-top: 20px; font-size: 14px; color: #777; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"container\">"
                + "<div class=\"header\">"
                + "<h2>Welcome to Our App!</h2>"
                + "</div>"
                + "<div class=\"content\">"
                + "<p>Please enter the verification code below to continue:</p>"
                + "<div class=\"verification-code\">" + verificationCode + "</div>"
                + "</div>"
                + "<div class=\"footer\">"
                + "<p>If you didn't request this email, please ignore it.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        }catch(MessagingException e) {
            e.printStackTrace();
        }
    }


    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void verifyUser(VerifyUserRequest request) throws RuntimeException{
        var optionalUser = userRepository.findByVerificationCode(request.getVerificationCode());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationCodeExpiration().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Verification code expired");

            user.setAccountVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiration(null);
            userRepository.save(user);

        }else
            throw new RuntimeException("User not found.");

    }

    public void resendVerificationCode(String email) throws RuntimeException{
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isAccountVerified()) {
                throw new RuntimeException("User is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepository.save(user);
        }else {
            throw new RuntimeException("User not found");
        }
    }

    public void resetPasswordRequest(String email) {
        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            String token = generateResetToken();
            user.setResetToken(token);
            userRepository.save(user);

            sendResetEmail(user, token);
        }

    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public boolean isTokenValid(String token) {
        var optionalUser = userRepository.findByResetToken(token);
        return optionalUser.isPresent();
    }

    private void sendResetEmail(User user, String token) {
        String subject = "Password Reset Request";
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        String message = "To reset your password, click on the link below:\n" + resetLink;

        try {
            emailService.sendSimpleEmail(user.getEmail(), subject, message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void resetPassword(String token, ResetPasswordRequest request) {
        var optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isPresent()) {
            var user = optionalUser.get();

            if (request.getPassword().equals(request.getConfirmPassword())) {
                user.setPassword(encoder.encode(request.getPassword()));
                user.setResetToken(null);
                userRepository.save(user);
            }else
                throw new RuntimeException("Passwords do not match.");
        }else
            throw new RuntimeException("Invalid or expired token");
    }
}
