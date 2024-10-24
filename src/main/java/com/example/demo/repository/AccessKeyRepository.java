package com.example.demo.repository;

import com.example.demo.model.AccessKey;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Integer> {
    List<AccessKey> findByUser(User user);
}
