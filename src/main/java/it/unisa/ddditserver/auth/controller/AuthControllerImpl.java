package it.unisa.ddditserver.auth.controller;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.auth.exceptions.ExistingUserException;
import it.unisa.ddditserver.auth.exceptions.InvalidCredentialsException;
import it.unisa.ddditserver.auth.exceptions.LoggedUserException;
import it.unisa.ddditserver.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AuthControllerImpl implements AuthController {
    @Autowired
    private AuthService authService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO user, HttpServletRequest request) {
        // Retrieve of token if it exists
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        try {
            return authService.signup(user, token);
        } catch (InvalidCredentialsException | ExistingUserException | LoggedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (AuthException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", e.getMessage()));
        }
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user, HttpServletRequest request) {
        // Retrieve of token if it exists
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        try {
            return authService.login(user, token);
        } catch (InvalidCredentialsException | ExistingUserException | LoggedUserException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (AuthException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", e.getMessage()));
        }
    }
}
