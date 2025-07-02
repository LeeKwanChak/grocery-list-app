package com.marcus.grocerylist.service;

import com.marcus.grocerylist.exception.ResourceNotFoundException;
import com.marcus.grocerylist.exception.UnauthorizedAccessException;
import com.marcus.grocerylist.exception.UserAlreadyExistsException;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User("authenticatedUser", "rawPassword", "auth@example.com");
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUser = userRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getUsername(), null, Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRegisterNewUserSuccess() {
        User newUser = new User("newUser", "newPass", "new@example.com");

        User registeredUser = userService.registerNewUser(newUser);

        assertNotNull(registeredUser.getId());
        assertEquals("newUser", registeredUser.getUsername());
        assertTrue(passwordEncoder.matches("newPass", registeredUser.getPassword()));

        Optional<User> foundUser = userRepository.findByUsername("newUser");
        assertTrue(foundUser.isPresent());
        assertEquals(registeredUser.getId(), foundUser.get().getId());
    }

    @Test
    void testRegisterNewUserExistingUsernameThrowsException() {
        User existingUser = new User("authenticatedUser", "somePass", "duplicate@example.com");

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerNewUser(existingUser));

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testRegisterNewUserExistingEmailThrowsException() {
        User existingEmailUser = new User("anotherUser", "somePass", "auth@example.com");

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerNewUser(existingEmailUser));

        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testUpdateUserSuccess() {
        User updatedInfo = new User("updatedUser", "newPass", "updated@example.com");
        updatedInfo.setPassword("newPass");

        User resultUser = userService.updateUser(testUser.getId(), updatedInfo);

        assertNotNull(resultUser);
        assertEquals(testUser.getId(), resultUser.getId());
        assertEquals("updatedUser", resultUser.getUsername());
        assertEquals("updated@example.com", resultUser.getEmail());
    }
}