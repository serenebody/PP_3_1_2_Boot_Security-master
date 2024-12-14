package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.AdminService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Validated
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final RoleService roleService;

    @Autowired
    public AdminController(AdminService adminService, RoleService roleService) {
        this.adminService = adminService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showUsers(Model model, Authentication auth) {
        List<User> users = adminService.getAllUsers();
        String username = auth.getName();
        User user = adminService.getUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "main";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "main";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user,
                           @RequestParam(value = "roleNames", required = false) List<String> roleNames) {
        System.out.println("Received user: " + user);
        System.out.println("Received roles: " + roleNames);

        Set<Role> roles = roleService.getUserRoles(roleNames);
        user.setRoles(roles);

        adminService.saveUser (user);
        return "redirect:/admin";
    }
    @GetMapping("/update")
    public String showUpdateUserForm(@RequestParam Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "main";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id, @Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             @RequestParam(value = "roleNames", required = false) List<String> roles,
                             @RequestParam(value = "password", required = false) String rawPassword) {
        if (result.hasErrors()) {
            return "main";
        }
        Set<Role> roleSet = roleService.getUserRoles(roles);
        user.setRoles(roleSet);
        adminService.updateUser(user, rawPassword);
        return "redirect:/admin";
    }
}
