package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when authentication fails due to invalid password.
 *
 * This subclass of {@link AuthException} indicates that the provided password is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidPasswordException extends AuthException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
