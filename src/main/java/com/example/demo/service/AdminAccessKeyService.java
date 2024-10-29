package com.example.demo.service;


import com.example.demo.model.AccessKey;
import com.example.demo.repository.AccessKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAccessKeyService {

    @Autowired
    private AccessKeyRepository repository;


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
}
