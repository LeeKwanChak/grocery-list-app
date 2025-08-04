package com.marcus.grocerylist.service;

import com.marcus.grocerylist.exception.ResourceNotFoundException;
import com.marcus.grocerylist.exception.UserAlreadyExistsException;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import com.marcus.grocerylist.exception.UnauthorizedAccessException;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User registerNewUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " already exist.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + user.getEmail() + "' already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        User deleteUser = user.get();
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!deleteUser.getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this user.");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public User updateUser(Long userId, User updatedUserInfo){
         Optional<User> user = userRepository.findById(userId);
         if(user.isEmpty()){
             throw new ResourceNotFoundException("User not found with ID: " + userId);
         }
         User updateUser = user.get();
         String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!updateUser.getUsername().equals(currentUsername)) {
            throw new UnauthorizedAccessException("You are not authorized to update this user's profile.");
        }

        if (updatedUserInfo.getUsername() != null && !updateUser.getUsername().equals(updatedUserInfo.getUsername())) {
            if (userRepository.existsByUsername(updatedUserInfo.getUsername())) {
                throw new UserAlreadyExistsException("Username '" + updatedUserInfo.getUsername() + "' already exists.");
            }
            updateUser.setUsername(updatedUserInfo.getUsername());
        }

        if (updatedUserInfo.getEmail() != null && !updatedUserInfo.getEmail().equals(updateUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUserInfo.getEmail())) {
                throw new UserAlreadyExistsException("Email '" + updatedUserInfo.getEmail() + "' already exists.");
            }
            updateUser.setEmail(updatedUserInfo.getEmail());
        }

        if (updatedUserInfo.getPassword() != null && !updatedUserInfo.getPassword().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(updatedUserInfo.getPassword()));
        }

        return userRepository.save(updateUser);
    }

}