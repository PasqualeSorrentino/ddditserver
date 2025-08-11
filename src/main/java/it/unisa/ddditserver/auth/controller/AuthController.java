package it.unisa.ddditserver.auth.controller;

import it.unisa.ddditserver.auth.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller interface for authentication endpoints.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-11
 */
public interface AuthController {

    /**
     * Handles user signup requests.
     *
     * @param user the user data transfer object containing signup information
     * @param request the HTTP servlet request object
     * @return a ResponseEntity indicating the result of the signup operation
     */
    ResponseEntity<?> signup(@RequestBody UserDTO user, HttpServletRequest request);

    /**
     * Handles user login requests.
     *
     * @param user the user data transfer object containing login information
     * @param request the HTTP servlet request object
     * @return a ResponseEntity indicating the result of the login operation
     */
    ResponseEntity<?> login(@RequestBody UserDTO user, HttpServletRequest request);

    /**
     * Handles user logout requests.
     *
     * @param request the HTTP servlet request object
     * @return a ResponseEntity indicating the result of the logout operation
     */
    ResponseEntity<?> logout(HttpServletRequest request);


}


