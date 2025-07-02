package com.marcus.grocerylist.controller;

import com.marcus.grocerylist.exception.UserAlreadyExistsException;
import com.marcus.grocerylist.jwt.JwtUtil;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.dto.AuthRequest;
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
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest request) {
        try {
            User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());

            userService.registerNewUser(newUser);

            return new ResponseEntity<>(Map.of("message", "User registered successfully"), HttpStatus.CREATED);

        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "An unexpected error occurred during registration: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(authentication.getName());
        return new AuthResponse(token);
    }
}