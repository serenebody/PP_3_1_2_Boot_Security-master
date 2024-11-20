package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;
import java.util.Set;

@Service
public interface RoleService {
    Role findByName(String name);
    List<Role> findAll();
    Role findById(Long id);
    Set<Role> getUserRoles(List<String> roles);
}
