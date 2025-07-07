package com.marcus.grocerylist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcus.grocerylist.dto.RegistrationRequest;
import com.marcus.grocerylist.exception.UserAlreadyExistsException;
import com.marcus.grocerylist.jwt.JwtUtil;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testRegisterUserSuccess() throws Exception {
        RegistrationRequest authRequest = new RegistrationRequest("testUser", "password123", "test@example.com");

        when(userService.registerNewUser(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(userService, times(1)).registerNewUser(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExistsByUsername() throws Exception {
        RegistrationRequest authRequest = new RegistrationRequest("existingUser", "password123", "exist@example.com");
        String errorMessage = "Username existingUser already exist.";

        doThrow(new UserAlreadyExistsException(errorMessage))
                .when(userService).registerNewUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(userService, times(1)).registerNewUser(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExistsByEmail() throws Exception {
        RegistrationRequest authRequest = new RegistrationRequest("newUser", "password123", "existing@example.com");
        String errorMessage = "Email 'existing@example.com' already exists.";

        doThrow(new UserAlreadyExistsException(errorMessage))
                .when(userService).registerNewUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(userService, times(1)).registerNewUser(any(User.class));
    }

    @Test
    void testRegisterUserUnexpectedError() throws Exception {
        RegistrationRequest authRequest = new RegistrationRequest("anyUser", "anyPass", "any@example.com");
        String errorMessage = "Some unexpected error.";

        doThrow(new RuntimeException(errorMessage))
                .when(userService).registerNewUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: " + errorMessage));

        verify(userService, times(1)).registerNewUser(any(User.class));
    }

    @Test
    void testLoginSuccess() throws Exception {
        RegistrationRequest authRequest = new RegistrationRequest("test@example.com", "password123");

        String expectedToken = "mocked.jwt.token";
        String testUserEmail = "test@example.com";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUserEmail);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtil.generateToken(testUserEmail)).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(testUserEmail, "password123")
        );
        verify(jwtUtil, times(1)).generateToken(testUserEmail);
    }

}