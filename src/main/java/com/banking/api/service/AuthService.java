package com.banking.api.service;

import com.banking.api.dto.*;
import com.banking.api.model.Account;
import com.banking.api.model.User;
import com.banking.api.repository.AccountRepository;
import com.banking.api.repository.UserRepository;
import com.banking.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) throw new RuntimeException("Username exists");
        if (userRepository.existsByEmail(req.getEmail())) throw new RuntimeException("Email exists");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .build();
        user = userRepository.save(user);

        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .user(user)
                .balance(BigDecimal.valueOf(1000)) // welcome bonus R1000
                .build();
        accountRepository.save(account);

        return jwtUtil.generateToken(user.getUsername());
    }

    public String login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}