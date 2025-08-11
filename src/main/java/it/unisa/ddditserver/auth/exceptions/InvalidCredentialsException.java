package it.unisa.ddditserver.auth.exceptions;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 *
 * This subclass of {@link AuthException} indicates that the provided username or password
 * is malformed.
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