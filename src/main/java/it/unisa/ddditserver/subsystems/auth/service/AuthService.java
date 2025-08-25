package it.unisa.ddditserver.subsystems.auth.service;

import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for authentication-related operations.
 * Provides methods to register, authenticate, and logout users in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-11
 */
public interface AuthService {

    /**
     * Registers a new user with the provided user data.
     *
     * @param user the data transfer object containing signup information
     * @param token an optional authentication token, if applicable
     * @return a ResponseEntity containing a map with relevant response data
     */
    ResponseEntity<Map<String, String>> signup(UserDTO user, String token);

    /**
     * Authenticates a registered user with the provided user data and an optional authentication token.
     *
     * @param user the data transfer object containing login information
     * @param token an optional authentication token, if applicable
     * @return a ResponseEntity containing a map with relevant response data
     */
    ResponseEntity<Map<String, String>> login(UserDTO user, String token);

    /**
     * Logs out the user by revoking the provided authentication token.
     *
     * @param token the authentication token to be revoked
     * @return a ResponseEntity containing a map with relevant response data
     */
    ResponseEntity<Map<String, String>> logout(String token);
}
