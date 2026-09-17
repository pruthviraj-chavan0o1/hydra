package com.pruthviraj.hydra.controller;

import com.pruthviraj.hydra.model.User;
import com.pruthviraj.hydra.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class HydraController {

    private final UserRepository userRepository;

    public HydraController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/status")
    public String status() {
        return "HYDRA backend is working successfully!";
    }

    @PostMapping("/api/register")
    public String register(@RequestBody Map<String, String> registerData) {

        String username = registerData.get("username");
        String email = registerData.get("email");
        String password = registerData.get("password");

        if (username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank()) {

            return "Please fill all fields.";
        }

        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            return "Username already exists.";
        }

        Optional<User> existingEmail = userRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            return "Email already exists.";
        }

        User newUser = new User(username, email, password);

        userRepository.save(newUser);

        return "Registration successful for " + username;
    }

    @PostMapping("/api/login")
    public String login(@RequestBody Map<String, String> loginData) {

        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            if (user.getPassword().equals(password)) {
                return "Login successful! Welcome to Hydra.";
            }
        }

        return "Invalid username or password.";
    }
}