package com.marcus.grocerylist.service;

import org.springframework.security.core.userdetails.User;
import com.marcus.grocerylist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.marcus.grocerylist.exception.EmailNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        com.marcus.grocerylist.model.User appUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found: " + email));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities("USER")
                .build();
    }
}
