package ru.kata.spring.boot_security.demo.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Transactional
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void saveUser(@Valid User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Пользователь должен быть уникальным!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(@Valid Long id) {
        User user = getUserById(id);
        if (user != null) {
            userRepository.deleteById(id);
        } else throw new EntityNotFoundException("Пользовыатель с id " + id + " не найден для удаления!");
    }

    @Override
    public void updateUser (@Valid User user, String rawPassword) {
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
        userRepository.save(user);
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
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователя с таким ID не существует!"));
    }
}
