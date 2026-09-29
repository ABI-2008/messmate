package com.sece.expert.service;

import com.sece.expert.dto.AuthResponse;
import com.sece.expert.dto.SignInRequest;
import com.sece.expert.dto.SignUpRequest;
import com.sece.expert.entity.User;
import com.sece.expert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(SignUpRequest request) {
        String cleanUsername = request.getUsername().trim().toLowerCase();
        String cleanEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(cleanUsername)) {
            return new AuthResponse(false, "Username '" + cleanUsername + "' is already taken.");
        }

        if (userRepository.existsByEmail(cleanEmail)) {
            return new AuthResponse(false, "Email '" + cleanEmail + "' is already registered.");
        }

        String hashedPassword = hashPassword(request.getPassword());

        String assignedRole = request.getRole() != null && !request.getRole().isBlank() 
                ? request.getRole().toUpperCase().trim() 
                : "STUDENT";

        User user = new User(
                cleanUsername,
                request.getFullName().trim(),
                cleanEmail,
                hashedPassword,
                assignedRole
        );

        User saved = userRepository.save(user);
        return new AuthResponse(true, "Registration successful! Welcome to MessMate.", saved);
    }

    public AuthResponse login(SignInRequest request) {
        String identifier = request.getIdentifier().trim().toLowerCase();
        String rawPassword = request.getPassword();

        // Allow login by either username or email
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }

        if (userOpt.isEmpty()) {
            return new AuthResponse(false, "Invalid username or password.");
        }

        User user = userOpt.get();
        String hashedInputPassword = hashPassword(rawPassword);

        // Support hashed password comparison and legacy plain text fallback
        boolean matches = user.getPassword().equals(hashedInputPassword) || user.getPassword().equals(rawPassword);
        if (!matches) {
            return new AuthResponse(false, "Invalid username or password.");
        }

        return new AuthResponse(true, "Sign in successful!", user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback
        }
    }
}
