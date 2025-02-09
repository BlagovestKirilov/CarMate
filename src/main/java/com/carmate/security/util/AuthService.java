package com.carmate.security.util;

import com.carmate.entity.account.Account;
import com.carmate.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AccountRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(String email, String password) {
        Optional<Account> existingUser = userRepository.findByUsername(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        Account newUser = new Account();
        newUser.setUsername(email);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);

        return jwtUtil.generateToken(email);
    }

    public String login(String email, String password) {
        Account user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getToken() != null && jwtUtil.validateToken(user.getToken()) != null) {
            return user.getToken(); // Return existing valid token
        }

        // Generate new token and save
        String newToken = jwtUtil.generateToken(email);
        user.setToken(newToken);
        userRepository.save(user);
        return newToken;
    }
}
