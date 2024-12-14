package ru.kata.spring.boot_security.demo.service;


import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

@Service
public interface AdminService {
    void saveUser(User user);
    void deleteUser(Long id);
    void updateUser(User user, String rawPassword);
    List<User> getAllUsers();
    User getUserByUsername(String username);
    User getUserById(Long id);
}
