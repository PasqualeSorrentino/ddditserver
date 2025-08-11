package it.unisa.ddditserver.auth.controller;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.auth.exceptions.ExistingUserException;
import it.unisa.ddditserver.auth.exceptions.InvalidCredentialsException;
import it.unisa.ddditserver.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AuthControllerImpl implements AuthController {
    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<?> signup(@RequestBody UserDTO user) {
        try {
            return authService.signup(user);
        } catch (InvalidCredentialsException | ExistingUserException e) {
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
