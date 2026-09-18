package com.pruthviraj.hydra.controller;

import com.pruthviraj.hydra.model.User;
import com.pruthviraj.hydra.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class HydraController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public HydraController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Backend status
    @GetMapping("/api/status")
    public String status() {
        return "HYDRA backend is working successfully!";
    }

    // Register user
    @PostMapping("/api/register")
    public String register(
            @RequestBody Map<String, String> registerData) {

        String username = registerData.get("username");
        String email = registerData.get("email");
        String password = registerData.get("password");

        if (username == null || email == null || password == null ||
                username.isBlank() ||
                email.isBlank() ||
                password.isBlank()) {

            return "Please fill all fields.";
        }

        Optional<User> existingUser =
                userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            return "Username already exists.";
        }

        Optional<User> existingEmail =
                userRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            return "Email already exists.";
        }

        String encryptedPassword =
                passwordEncoder.encode(password);

        User newUser = new User(
                username,
                email,
                encryptedPassword
        );

        userRepository.save(newUser);

        return "Registration successful for " + username;
    }

    // Login user
    @PostMapping("/api/login")
    public String login(
            @RequestBody Map<String, String> loginData) {

        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null ||
                username.isBlank() ||
                password.isBlank()) {

            return "Please enter username and password.";
        }

        Optional<User> userOptional =
                userRepository.findByUsername(username);

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            boolean passwordMatches =
                    passwordEncoder.matches(
                            password,
                            user.getPassword()
                    );

            if (passwordMatches) {
                return "Login successful! Welcome to Hydra.";
            }
        }

        return "Invalid username or password.";
    }

    // Get all users for admin
    @GetMapping("/api/admin/users")
    public List<Map<String, Object>> getAllUsers() {

        return createSafeUserList();
    }

    // Get users for dashboard
    @GetMapping("/api/users")
    public List<Map<String, Object>> getUsers() {

        return createSafeUserList();
    }

    // Common method to prepare safe user data
    private List<Map<String, Object>> createSafeUserList() {

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> safeUsers =
                new ArrayList<>();

        for (User user : users) {

            Map<String, Object> userData =
                    new HashMap<>();

            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());

            safeUsers.add(userData);
        }

        return safeUsers;
    }
}