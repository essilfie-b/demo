package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.demo.model.AccessKey;
import com.example.demo.service.UserAccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin
@PreAuthorize("hasAuthority('user:read') or hasAuthority('user:create')")
public class UserAccessKeyController {

    @Autowired
    UserAccessKeyService userAccessKeyService;

    @GetMapping("accesskeys")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<AccessKey>> getAccessKeys() {
        var accessKeys = userAccessKeyService.getAccessKeys();
        System.out.println("Access Keys: " + accessKeys);
        return new ResponseEntity<>(accessKeys, HttpStatus.OK);
    }

    @PostMapping("accesskeys/request")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<AccessKey> generateAccessKey() {
        var accessKey = userAccessKeyService.generateAccessKey();
        System.out.println("Access key: " + accessKey);
        return new ResponseEntity<>(accessKey, HttpStatus.OK);

    }

    @GetMapping("accesskeys/{accessKeyId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<AccessKey> getKeyById(@PathVariable int accessKeyId) {
        var targetAccessKey = userAccessKeyService.getKeyById(accessKeyId);
        if (targetAccessKey != null)
            return new ResponseEntity<>(targetAccessKey, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}