package com.example.demo.service;


import com.example.demo.model.AccessKey;
import com.example.demo.model.User;
import com.example.demo.repository.AccessKeyRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccessKeyService {

    private static final Logger logger = LoggerFactory.getLogger(UserAccessKeyService.class);

    @Autowired
    AccessKeyRepository accessKeyRepository;

    @Autowired
    UserRepository userRepository;


    public List<AccessKey> getAccessKeys() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        logger.info("Fetching access keys for user: {}", username);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            logger.warn("No user found for username: {}", username);
            return List.of(); // Return an empty list if no user found
        }

        List<AccessKey> keys = accessKeyRepository.findByUser(user);
        logger.info("Found {} access keys for user: {}", keys.size(), username);
        return keys;
    }

    public AccessKey generateAccessKey() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        logger.info("Generating access key for user: {}", username);

        var accessKeys = getAccessKeys();
        var existingActiveKey = accessKeys.stream()
                .filter(key -> key.getStatus() == AccessKey.Status.ACTIVE)
                .findFirst();

        if (existingActiveKey.isEmpty()) {
            var accessKey = new AccessKey();
            accessKey.setUser(user); // Associate with the correct user
            logger.info("Creating a new access key for user: {}", username);
            return accessKeyRepository.save(accessKey);
        }

        logger.info("Returning existing active key for user: {}", username);
        return existingActiveKey.get();
    }


    public AccessKey getKeyById(int accessKeyId) {
        return accessKeyRepository.findById(accessKeyId).orElse(null);

    }


}
