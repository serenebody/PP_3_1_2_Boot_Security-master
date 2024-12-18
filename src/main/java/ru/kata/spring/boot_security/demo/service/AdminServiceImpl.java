package ru.kata.spring.boot_security.demo.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public void saveUser(@Valid User user, List<String> roleNames) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Пользователь должен быть уникальным!");
        }
        Set<Role> roles = roleService.getUserRoles(roleNames);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void updateUser(@Valid User user, String rawPassword, List<String> roleNames) {
        User userFromDB = getUserById(user.getId());
        if (!userFromDB.getUsername().equals(user.getUsername())) {
            userRepository.findByUsername(user.getUsername())
                    .ifPresent(u -> {
                        throw new DataIntegrityViolationException("Имя должно быть уникальным!");
                    });
        }
        if (rawPassword != null && !rawPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            user.setPassword(userFromDB.getPassword());
        }
        if (roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = roleService.getUserRoles(roleNames);
            user.setRoles(roles);
        } else {
            user.setRoles(userFromDB.getRoles());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(@Valid Long id) {
        User user = getUserById(id);
        if (user != null) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден для удаления!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!"));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с таким ID не существует!"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsersWithRolesAsString() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            String rolesAsString = user.getRoles().stream()
                    .map(Role::getRole)
                    .collect(Collectors.joining(", "));
            user.setRolesAsString(rolesAsString);
        });
        return users;
    }
}
