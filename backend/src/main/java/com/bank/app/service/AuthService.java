package com.bank.app.service;

import com.bank.app.dto.LoginRequest;
import com.bank.app.dto.LoginResponse;
import com.bank.app.entity.User;
import com.bank.app.repository.UserRepository;
import com.bank.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String role = user.getRole().getRoleName();

        String token = jwtService.generateToken(userDetails, role);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(role)
                .build();
    }
}
