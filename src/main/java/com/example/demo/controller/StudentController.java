package com.example.demo.controller;


import com.example.demo.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class StudentController {

    List<Student> students = new ArrayList<>(List.of(new Student(1, "Henry", 79), new Student(2, "Eric", 69)));

    @GetMapping("students")
    public List<Student> getStudents() {
        return students;
    }

    @GetMapping("csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("/")
    public String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }

    @PostMapping("students")
    public Student addStudent(@RequestBody Student student) {
        students.add(student);
        return student;
    }


}
