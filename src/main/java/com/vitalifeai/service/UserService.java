/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author wutyihlaingwin
 */
package com.vitalifeai.service;

import com.vitalifeai.entity.User;
import com.vitalifeai.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Return all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Register a new user
    public String registerUser(User user) {

        String normalizedEmail =
                user.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            return "Email already exists";
        }

        user.setFullName(user.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        userRepository.save(user);

        return "Success";
    }

    // Authenticate a user during login
    public User login(String email, String password) {

        String normalizedEmail =
                email.trim().toLowerCase();

        Optional<User> optionalUser =
                userRepository.findByEmail(normalizedEmail);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            if (passwordEncoder.matches(
                    password,
                    user.getPassword())) {

                return user;
            }
        }

        return null;
    }

    // Find a user by database ID
    public User getUserById(Long userId) {

        if (userId == null) {
            return null;
        }

        return userRepository
                .findById(userId)
                .orElse(null);
    }
}