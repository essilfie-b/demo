package com.example.demo.service;


import com.example.demo.model.AccessKey;
import com.example.demo.repository.AccessKeyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.model.AccessKey.Status.ACTIVE;

@Service
public class AdminAccessKeyService {

    @Autowired
    private AccessKeyRepository repository;

    @Autowired
    private UserRepository userRepository;


    public List<AccessKey> getAllKeys() {
        return repository.findAll();
    }

    public AccessKey getKeyById(int accessKeyId) {
        return repository.findById(accessKeyId).orElse(null);
    }

    public AccessKey revokeAccessKey(AccessKey targetAccessKey) {
        targetAccessKey.setStatus(AccessKey.Status.REVOKED);
        return repository.save(targetAccessKey);
    }

    public List<AccessKey> getUserActiveKeys(String email) {
        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            return repository.findByUser(user)
                    .stream()
                    .filter(activeKey -> activeKey.getStatus() == ACTIVE)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
