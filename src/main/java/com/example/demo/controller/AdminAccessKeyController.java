package com.example.demo.controller;


import com.example.demo.model.AccessKey;
import com.example.demo.service.AdminAccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@PreAuthorize("hasAuthority('admin:read') or hasAuthority('admin:create') or hasAuthority('admin:update') or hasAuthority('admin:delete')")
public class AdminAccessKeyController {

    @Autowired
    private AdminAccessKeyService service;

    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<AccessKey>> getAllKeys() {
        return ResponseEntity.ok(service.getAllKeys());
    }

    @PutMapping("accesskeys/revoke/{accessKeyId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<AccessKey> revokeAccessKey(@PathVariable int accessKeyId) {
        var targetAccessKey = service.getKeyById(accessKeyId);
        if (targetAccessKey != null) {
            var revokedAccessKey = service.revokeAccessKey(targetAccessKey);
            return new ResponseEntity<>(revokedAccessKey, HttpStatus.OK);
        }
        else
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
