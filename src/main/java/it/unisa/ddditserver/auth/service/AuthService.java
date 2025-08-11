package it.unisa.ddditserver.auth.service;

import it.unisa.ddditserver.auth.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for authentication-related operations.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-11
 */
public interface AuthService {

    /**
     * Registers a new user with the provided user data and an optional token.
     *
     * @param user the user data transfer object containing signup information
     * @param token an optional authentication token, if applicable
     * @return a ResponseEntity containing a map with relevant response data, such as a JWT token
     */
    ResponseEntity<Map<String, String>> signup(UserDTO user, String token);

    /**
     * Authenticates a registered user with the provided user data and an optional token.
     *
     * @param user the user data transfer object containing login information
     * @param token an optional authentication token, if applicable
     * @return a ResponseEntity containing a map with relevant response data, such as a JWT token
     */
    ResponseEntity<Map<String, String>> login(UserDTO user, String token);

    /**
     * Execute the logout of a user and revoke his token.
     *
     * @param token the authentication token
     * @return a ResponseEntity containing a map with relevant response data, such as the result of the operation
     */
    ResponseEntity<Map<String, String>> logout(String token);
}

