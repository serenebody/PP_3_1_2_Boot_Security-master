package ru.kata.spring.boot_security.demo.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.AdminService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final AdminService adminService;

    @Autowired
    public UserController(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Authentication auth) {
        String username = auth.getName();
        User user = adminService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}

