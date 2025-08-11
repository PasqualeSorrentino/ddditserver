package it.unisa.ddditserver.auth.service;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.*;
import it.unisa.ddditserver.db.cosmos.auth.CosmosAuthService;
import it.unisa.ddditserver.db.gremlin.auth.GremlinAuthService;
import it.unisa.ddditserver.validators.ValidationResult;
import it.unisa.ddditserver.validators.user.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private GremlinAuthService gremlinService;

    @Mock
    private CosmosAuthService cosmosAuthService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        authService.jwtSecretBase64 = "NnUvMZ1Gz5QJyN5IM2H5wJrR2yxUd+ZmBPtZ/jZJChA=";
        authService.init();
    }

    // Signup unit testing

    @Test
    void signupSuccess() {
        UserDTO user = new UserDTO();
        user.setUsername("user1");
        user.setPassword("password");

        ValidationResult validResult = mock(ValidationResult.class);
        when(validResult.isValid()).thenReturn(true);

        when(userValidator.validateLoggedStatus(null)).thenReturn(validResult);
        when(userValidator.validateCredentials(any())).thenReturn(validResult);
        when(userValidator.validateExistence(any(), eq(false))).thenReturn(validResult);

        doNothing().when(gremlinService).saveUser(any());

        ResponseEntity<Map<String, String>> response = authService.signup(user, null);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("token"));
        verify(gremlinService, times(1)).saveUser(any());
    }

    @Test
    void signupThrowsLoggedUserException() {
        UserDTO user = new UserDTO();

        ValidationResult invalidResult = mock(ValidationResult.class);
        when(invalidResult.isValid()).thenReturn(false);
        when(invalidResult.getMessage()).thenReturn("User already logged");

        when(userValidator.validateLoggedStatus(null)).thenReturn(invalidResult);

        LoggedUserException e = assertThrows(LoggedUserException.class, () -> authService.signup(user, null));
        assertEquals("User already logged", e.getMessage());
    }

    @Test
    void signupThrowsInvalidCredentialsException() {
        UserDTO user = new UserDTO("testuser", "password");

        ValidationResult validLoggedStatus = mock(ValidationResult.class);
        when(validLoggedStatus.isValid()).thenReturn(true);

        ValidationResult invalidCredentials = mock(ValidationResult.class);
        when(invalidCredentials.isValid()).thenReturn(false);
        when(invalidCredentials.getMessage()).thenReturn("Invalid credentials");

        when(userValidator.validateLoggedStatus(null)).thenReturn(validLoggedStatus);
        when(userValidator.validateCredentials(any())).thenReturn(invalidCredentials);

        InvalidCredentialsException e = assertThrows(InvalidCredentialsException.class, () -> authService.signup(user, null));
        assertEquals("Invalid credentials", e.getMessage());
    }

    @Test
    void signupThrowsExistingUserException() {
        UserDTO user = new UserDTO();
        user.setUsername("existingUser");
        user.setPassword("password");


        ValidationResult validLoggedStatus = mock(ValidationResult.class);
        when(validLoggedStatus.isValid()).thenReturn(true);

        ValidationResult invalidExistence = mock(ValidationResult.class);
        when(invalidExistence.isValid()).thenReturn(false);
        when(invalidExistence.getMessage()).thenReturn("User already exists");

        when(userValidator.validateLoggedStatus(null)).thenReturn(validLoggedStatus);
        when(userValidator.validateCredentials(any())).thenReturn(validLoggedStatus);
        when(userValidator.validateExistence(any(), eq(false))).thenReturn(invalidExistence);

        ExistingUserException e = assertThrows(ExistingUserException.class, () -> authService.signup(user, null));
        assertEquals("User already exists", e.getMessage());
    }

    // Login unit testing

    @Test
    void loginSuccess() {
        UserDTO user = new UserDTO();
        user.setUsername("user1");
        user.setPassword("password");

        ValidationResult validResult = mock(ValidationResult.class);
        when(validResult.isValid()).thenReturn(true);

        when(userValidator.validateLoggedStatus(null)).thenReturn(validResult);
        when(userValidator.validateCredentials(any())).thenReturn(validResult);
        when(userValidator.validateExistence(any(), eq(true))).thenReturn(validResult);
        when(userValidator.validateMatchingPasswords(any())).thenReturn(validResult);

        ResponseEntity<Map<String, String>> response = authService.login(user, null);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("token"));
    }

    @Test
    void loginThrowsLoggedUserException() {
        UserDTO user = new UserDTO("testuser", "password");

        ValidationResult invalidLoggedStatus = mock(ValidationResult.class);
        when(invalidLoggedStatus.isValid()).thenReturn(false);
        when(invalidLoggedStatus.getMessage()).thenReturn("User already logged in");

        when(userValidator.validateLoggedStatus(null)).thenReturn(invalidLoggedStatus);

        LoggedUserException e = assertThrows(LoggedUserException.class, () -> authService.login(user, null));
        assertEquals("User already logged in", e.getMessage());
    }

    @Test
    void loginThrowsInvalidCredentialsException() {
        UserDTO user = new UserDTO("testuser", "password");

        ValidationResult validLoggedStatus = mock(ValidationResult.class);
        when(validLoggedStatus.isValid()).thenReturn(true);

        ValidationResult invalidCredentials = mock(ValidationResult.class);
        when(invalidCredentials.isValid()).thenReturn(false);
        when(invalidCredentials.getMessage()).thenReturn("Invalid credentials");

        when(userValidator.validateLoggedStatus(null)).thenReturn(validLoggedStatus);
        when(userValidator.validateCredentials(any())).thenReturn(invalidCredentials);

        InvalidCredentialsException e = assertThrows(InvalidCredentialsException.class, () -> authService.login(user, null));
        assertEquals("Invalid credentials", e.getMessage());
    }

    @Test
    void loginThrowsExistingUserException() {
        UserDTO user = new UserDTO("testuser", "password");

        ValidationResult validLoggedStatus = mock(ValidationResult.class);
        when(validLoggedStatus.isValid()).thenReturn(true);

        ValidationResult validCredentials = mock(ValidationResult.class);
        when(validCredentials.isValid()).thenReturn(true);

        ValidationResult invalidExistence = mock(ValidationResult.class);
        when(invalidExistence.isValid()).thenReturn(false);
        when(invalidExistence.getMessage()).thenReturn("User does not exist");

        when(userValidator.validateLoggedStatus(null)).thenReturn(validLoggedStatus);
        when(userValidator.validateCredentials(any())).thenReturn(validCredentials);
        when(userValidator.validateExistence(any(), eq(true))).thenReturn(invalidExistence);

        ExistingUserException e = assertThrows(ExistingUserException.class, () -> authService.login(user, null));
        assertEquals("User does not exist", e.getMessage());
    }

    @Test
    void loginThrowsPasswordsMismatchException() {
        UserDTO user = new UserDTO("testuser", "password");

        ValidationResult validLoggedStatus = mock(ValidationResult.class);
        when(validLoggedStatus.isValid()).thenReturn(true);

        ValidationResult validCredentials = mock(ValidationResult.class);
        when(validCredentials.isValid()).thenReturn(true);

        ValidationResult validExistence = mock(ValidationResult.class);
        when(validExistence.isValid()).thenReturn(true);

        ValidationResult invalidPasswordMatch = mock(ValidationResult.class);
        when(invalidPasswordMatch.isValid()).thenReturn(false);
        when(invalidPasswordMatch.getMessage()).thenReturn("Passwords do not match");

        when(userValidator.validateLoggedStatus(null)).thenReturn(validLoggedStatus);
        when(userValidator.validateCredentials(any())).thenReturn(validCredentials);
        when(userValidator.validateExistence(any(), eq(true))).thenReturn(validExistence);
        when(userValidator.validateMatchingPasswords(any())).thenReturn(invalidPasswordMatch);

        PasswordsMismatchException e = assertThrows(PasswordsMismatchException.class, () -> authService.login(user, null));
        assertEquals("Passwords do not match", e.getMessage());
    }

    // Logout unit testing

    @Test
    void logoutSuccess() throws AuthException {
        String token = "token123";

        doNothing().when(cosmosAuthService).blacklistToken(token);

        ResponseEntity<Map<String, String>> response = authService.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful, token blacklisted", response.getBody().get("message"));
        verify(cosmosAuthService, times(1)).blacklistToken(token);
    }

    @Test
    void logoutThrowsAuthException() throws AuthException {
        String token = "token123";

        doThrow(new RuntimeException("DB error")).when(cosmosAuthService).blacklistToken(token);

        AuthException e = assertThrows(AuthException.class, () -> authService.logout(token));
        assertEquals("DB error", e.getMessage());
    }
}
