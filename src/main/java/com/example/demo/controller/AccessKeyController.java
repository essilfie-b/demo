package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.demo.model.AccessKey;
import com.example.demo.service.AccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin
public class AccessKeyController {

    @Autowired
    AccessKeyService accessKeyService;

    @GetMapping("accesskeys")
    public ResponseEntity<List<AccessKey>> getAccessKeys() {
        var accessKeys = accessKeyService.getAccessKeys();
        System.out.println("Access Keys: " + accessKeys);
        return new ResponseEntity<>(accessKeys, HttpStatus.OK);
    }

    @PostMapping("accesskeys/request")
    public ResponseEntity<AccessKey> generateAccessKey() {
        var accessKey = accessKeyService.generateAccessKey();
        System.out.println("Access key: " + accessKey);
        return new ResponseEntity<>(accessKey, HttpStatus.OK);

    }

    @GetMapping("accesskeys/{accessKeyId}")
    public ResponseEntity<AccessKey> getKeyById(@PathVariable int accessKeyId) {
        var targetAccessKey = accessKeyService.getKeyById(accessKeyId);
        if (targetAccessKey != null)
            return new ResponseEntity<>(targetAccessKey, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("accesskeys/revoke/{accessKeyId}")
    public ResponseEntity<AccessKey> revokeAccessKey(@PathVariable int accessKeyId) {
        var targetAccessKey = accessKeyService.getKeyById(accessKeyId);
        if (targetAccessKey != null) {
            var revokedAccessKey = accessKeyService.revokeAccessKey(targetAccessKey);
            return new ResponseEntity<>(revokedAccessKey, HttpStatus.OK);
        }
        else
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}