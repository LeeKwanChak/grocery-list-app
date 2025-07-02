package com.marcus.grocerylist.service;

import com.marcus.grocerylist.exception.ResourceNotFoundException;
import com.marcus.grocerylist.exception.UnauthorizedAccessException;
import com.marcus.grocerylist.exception.UserAlreadyExistsException;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFindByIdFound() {
        Long userId = 1L;
        User mockUser = new User("testuser", "encodedPass", "test@example.com");
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindByIdNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(userId);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindByUsernameFound() {
        String username = "foundUser";
        User mockUser = new User(username, "encodedPass", "found@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsernameNotFound() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(username);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testSaveUser() {
        User userToSave = new User("newUser", "rawPass", "new@example.com");
        User savedUser = new User("newUser", "encodedPass", "new@example.com");
        savedUser.setId(10L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.save(userToSave);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void testUserExistsTrue() {
        String username = "existing";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertTrue(userService.userExists(username));
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void testUserExistsFalse() {
        String username = "nonexistent";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        assertFalse(userService.userExists(username));
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void testEmailExistsTrue() {
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        assertTrue(userService.emailExists(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testEmailExistsFalse() {
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        assertFalse(userService.emailExists(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testRegisterNewUserSuccess() {
        User newUser = new User("brandNewUser", "rawPassword", "brandnew@example.com");
        User savedUser = new User("brandNewUser", "encodedPassword", "brandnew@example.com");
        savedUser.setId(1L);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerNewUser(newUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository, times(1)).existsByUsername(newUser.getUsername());
        verify(userRepository, times(1)).existsByEmail(newUser.getEmail());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterNewUserUsernameAlreadyExists() {
        User existingUser = new User("existingUser", "rawPassword", "test@example.com");

        when(userRepository.existsByUsername(existingUser.getUsername())).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerNewUser(existingUser);
        });

        assertEquals("Username existingUser already exist.", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(existingUser.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterNewUserEmailAlreadyExists() {
        User existingEmailUser = new User("newUser", "rawPassword", "existing@example.com");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(existingEmailUser.getEmail())).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerNewUser(existingEmailUser);
        });

        assertEquals("Email 'existing@example.com' already exists.", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(existingEmailUser.getUsername());
        verify(userRepository, times(1)).existsByEmail(existingEmailUser.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        Long userId = 1L;
        User userToDelete = new User("currentUser", "encodedPass", "current@example.com");
        userToDelete.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        when(authentication.getName()).thenReturn("currentUser");

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteUserUnauthorized() {
        Long userId = 1L;
        User userToDelete = new User("otherUser", "encodedPass", "other@example.com");
        userToDelete.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        when(authentication.getName()).thenReturn("currentUser");

        Exception exception = assertThrows(UnauthorizedAccessException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("You are not authorized to delete this user.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateUserSuccess() {
        Long userId = 1L;
        User existingUser = new User("existingUser", "oldEncodedPass", "old@example.com");
        existingUser.setId(userId);

        User updatedUserInfo = new User("updatedUsername", "newRawPass", "updated@example.com");

        User savedUser = new User("updatedUsername", "newEncodedPass", "updated@example.com");
        savedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(authentication.getName()).thenReturn("existingUser");
        when(userRepository.existsByUsername("updatedUsername")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newRawPass")).thenReturn("newEncodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(userId, updatedUserInfo);

        assertNotNull(result);
        assertEquals("updatedUsername", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("newEncodedPass", result.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername("updatedUsername");
        verify(userRepository, times(1)).existsByEmail("updated@example.com");
        verify(passwordEncoder, times(1)).encode("newRawPass");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        Long userId = 99L;
        User updatedUserInfo = new User("any", "any", "any@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(userId, updatedUserInfo);
        });

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserUnauthorized() {
        Long userId = 1L;
        User existingUser = new User("otherUser", "pass", "other@example.com");
        existingUser.setId(userId);
        User updatedUserInfo = new User("otherUser", "newPass", "other@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(authentication.getName()).thenReturn("currentUser");

        Exception exception = assertThrows(UnauthorizedAccessException.class, () -> {
            userService.updateUser(userId, updatedUserInfo);
        });

        assertEquals("You are not authorized to update this user's profile.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserUsernameAlreadyExists() {
        Long userId = 1L;
        User existingUser = new User("existingUser", "oldPass", "old@example.com");
        existingUser.setId(userId);
        User updatedUserInfo = new User("takenUsername", "newPass", "old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(authentication.getName()).thenReturn("existingUser");
        when(userRepository.existsByUsername("takenUsername")).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updatedUserInfo);
        });

        assertEquals("Username 'takenUsername' already exists.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername("takenUsername");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserEmailAlreadyExists(){
        Long userId = 1L;
        User existingUser = new User("existingUser", "oldPass", "old@example.com");
        existingUser.setId(userId);
        User updatedUserInfo = new User("existingUser", "newPass", "taken@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(authentication.getName()).thenReturn("existingUser");
        when(userRepository.existsByUsername("existingUser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updatedUserInfo);
        });

        assertEquals("Email 'taken@example.com' already exists.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}