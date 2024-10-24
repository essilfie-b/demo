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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessKeyService {

    private static final Logger logger = LoggerFactory.getLogger(AccessKeyService.class);

    @Autowired
    AccessKeyRepository accessKeyRepository;

    @Autowired
    UserRepository userRepository;


    public List<AccessKey> getAccessKeys() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        logger.info("Fetching access keys for user: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("No user found for username: {}", username);
            return List.of(); // Return an empty list if no user found
        }

        List<AccessKey> keys = accessKeyRepository.findByUser(user);
        logger.info("Found {} access keys for user: {}", keys.size(), username);
        return keys;
    }

    public AccessKey generateAccessKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        logger.info("Generating access key for user: {}", username);

        var activeKeys = getAccessKeys();
        var existingActiveKey = activeKeys.stream()
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

    public AccessKey revokeAccessKey(AccessKey targetAccessKey) {
        targetAccessKey.setStatus(AccessKey.Status.REVOKED);
        return accessKeyRepository.save(targetAccessKey);
    }
}
