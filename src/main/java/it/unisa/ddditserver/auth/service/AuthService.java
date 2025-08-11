package it.unisa.ddditserver.auth.service;

import it.unisa.ddditserver.auth.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for authentication-related operations.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public interface AuthService {
    /**
     * Registers a new user with the given user data.
     *
     * @param user the user data transfer object containing signup information
     * @return a ResponseEntity containing a map with relevant response data (e.g., JWT token)
     */
    public ResponseEntity<Map<String, String>> signup(UserDTO user);
}
