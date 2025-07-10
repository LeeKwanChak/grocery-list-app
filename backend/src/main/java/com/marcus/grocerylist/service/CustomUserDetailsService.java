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
    public UserDetails loadUserByUsername(String username){
        com.marcus.grocerylist.model.User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities("USER")
                .build();
    }
}
