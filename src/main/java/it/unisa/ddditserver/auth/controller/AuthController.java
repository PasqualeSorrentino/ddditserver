package it.unisa.ddditserver.auth.controller;

import it.unisa.ddditserver.auth.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller interface for authentication endpoints.
 *
 * Defines the REST API operations related to user authentication.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public interface AuthController {
    /**
     * Handles user signup requests.
     *
     * @param user the user data transfer object containing signup information
     * @return a ResponseEntity indicating the result of the signup operation
     */
    ResponseEntity<?> signup(@RequestBody UserDTO user);
}

