package com.example.demo.service;

import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.LoginResponse;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    public User register(User user){
        List<User> allUsers = userRepository.findAll();

        User existingUsername = allUsers.stream()
                        .filter(existingUser -> existingUser.getUsername().equals(user.getUsername()))
                                .findFirst()
                .orElse(null);
        User existingEmail = allUsers.stream()
                .filter(existingMail -> existingMail.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        if (existingUsername == null && existingEmail == null) {
            user.setPassword(encoder.encode(user.getPassword()));
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiration(LocalDateTime.now().plusMinutes(15));
            user.setAccountVerified(false);
            sendVerificationEmail(user);
            User savedUser = userRepository.save(user);
            logger.info("Verification email sent to {}", user.getEmail());
            return savedUser;
        }
        else{
            logger.info("Username or email address already exists");
            return user;
        }

    }

    public LoginResponse verify(User user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if (authentication.isAuthenticated()){
            String token = jwtService.generateToken(user.getUsername());
            logger.info(token);
            return new LoginResponse(true, token, null);
        }
        return new LoginResponse(false, "Invalid username or password", null);
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

    public void verifyUser(VerifyUserDto request) throws RuntimeException{
        var optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationCodeExpiration().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Verification code expired");
            if (user.getVerificationCode().equals(request.getVerificationCode())){
                user.setAccountVerified(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiration(null);
                userRepository.save(user);
            }else
                throw new RuntimeException("Invalid verification code");

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

//    public String verify(User user) {
//        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
//        if (authentication.isAuthenticated())
//            return jwtService.generateToken(user.getUsername());
//        return "Failed";
//    }
    
    
}
