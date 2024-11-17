package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.AdminService;

@Controller
@RequestMapping("/user")
public class UserControllers {
    private final AdminService adminService;

    @Autowired
    public UserControllers(final AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication auth) {
        String username = auth.getName();
        User user = adminService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }
}

