package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when authentication fails due to invalid username.
 *
 * This subclass of {@link AuthException} indicates that the provided username is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidUsernameException extends AuthException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}