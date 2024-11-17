package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.AdminService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final RoleService roleService;

    @Autowired
    public AdminController(AdminService adminService, RoleService roleService, RoleRepository roleRepository) {
        this.adminService = adminService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/addUser";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user,
                          @RequestParam(value = "roleNames", required = false) String[] roleNames) {
        List<Role> roles = new ArrayList<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                Role role = roleService.findByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        user.setRoles(roles);
        adminService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/update")
    public String showUpdateUserForm(@RequestParam Long id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        model.addAttribute("role", roleService.findAll());
        return "admin/updateUser";
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             @RequestParam(value = "roleNames", required = false) List<String> roles) {

        if (roles != null) {
            List<Role> userRoles = new ArrayList<>();
            for (String roleName : roles) {
                Role role = roleService.findByName(roleName);
                userRoles.add(role);
            }
            user.setRoles(userRoles);
        }
        adminService.updateUser(user);
        return "redirect:/admin/users";
    }
}
