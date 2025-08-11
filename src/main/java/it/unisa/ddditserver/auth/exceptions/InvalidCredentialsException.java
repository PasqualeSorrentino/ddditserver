package it.unisa.ddditserver.auth.exceptions;

import java.util.List;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 *
 * This specific subtype of {@link AuthException} is used to indicate that
 * the provided username or password does not match any valid account in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}