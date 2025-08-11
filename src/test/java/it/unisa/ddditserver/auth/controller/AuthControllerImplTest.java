package it.unisa.ddditserver.auth.controller;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.auth.exceptions.ExistingUserException;
import it.unisa.ddditserver.auth.exceptions.InvalidCredentialsException;
import it.unisa.ddditserver.auth.exceptions.LoggedUserException;
import it.unisa.ddditserver.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerImplTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthControllerImpl authController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    // Signup unit testing

    @Test
    public void signupSuccess() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Signup successful")));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"testpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Signup successful"));
    }

    @Test
    public void signupInvalidCredentialsException() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    public void signupExistingUserException() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenThrow(new ExistingUserException("User already exists"));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existinguser\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already exists"));
    }

    @Test
    public void signupLoggedUserException() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenThrow(new LoggedUserException("User already logged in"));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"loggeduser\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already logged in"));
    }

    @Test
    public void signupAuthException() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenThrow(new AuthException("Authentication service error"));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Authentication service error"));
    }

    @Test
    public void signupUnexpectedException() throws Exception {
        Mockito.when(authService.signup(any(UserDTO.class), isNull()))
                .thenThrow(new RuntimeException("Unexpected exception"));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error"))
                .andExpect(jsonPath("$.details").value("Unexpected exception"));
    }

    // Login unit testing

    @Test
    public void loginSuccess() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Login successful")));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    public void loginInvalidCredentialsException() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    public void loginExistingUserException() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenThrow(new ExistingUserException("User already exists"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existinguser\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already exists"));
    }

    @Test
    public void loginLoggedUserException() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenThrow(new LoggedUserException("User already logged in"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"loggeduser\",\"password\":\"pass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already logged in"));
    }

    @Test
    public void loginAuthException() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenThrow(new AuthException("Authentication service error"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Authentication service error"));
    }

    @Test
    public void loginUnexpectedException() throws Exception {
        Mockito.when(authService.login(any(UserDTO.class), isNull()))
                .thenThrow(new RuntimeException("Unexpected exception"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error"))
                .andExpect(jsonPath("$.details").value("Unexpected exception"));
    }

    // Logout unit testing

    @Test
    public void logoutSuccess() throws Exception {
        String token = "token123";

        Mockito.when(authService.logout(eq(token)))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Logout successful")));

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    public void logoutMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/logout")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing or invalid Authorization header"));
    }

    @Test
    public void logoutAuthException() throws Exception {
        String token = "token123";

        Mockito.when(authService.logout(eq(token)))
                .thenThrow(new AuthException("Invalid token"));

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid token"));
    }

    @Test
    public void logoutUnexpectedException() throws Exception {
        String token = "token123";

        Mockito.when(authService.logout(eq(token)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error during logout"))
                .andExpect(jsonPath("$.details").value("Unexpected error"));
    }
}
