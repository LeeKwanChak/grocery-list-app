package com.marcus.grocerylist.controller;

import com.marcus.grocerylist.dto.LoginRequest;
import com.marcus.grocerylist.jwt.JwtUtil;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.dto.RegistrationRequest;
import com.marcus.grocerylist.dto.AuthResponse;
import com.marcus.grocerylist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationRequest request) {
            User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
            userService.registerNewUser(newUser);
            return new ResponseEntity<>(Map.of("message", "User registered successfully"), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String username = userService.findByEmail(request.getEmail())
                .map(User::getUsername) //same as .map(user -> user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new AuthResponse(token);
    }
}