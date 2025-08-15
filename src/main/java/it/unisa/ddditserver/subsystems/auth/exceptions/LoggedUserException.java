package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when a user tries to log in while already logged in.
 *
 * This exception extends {@link AuthException} and indicates
 * that the user is currently authenticated and cannot log in again.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public class LoggedUserException extends AuthException {
    public LoggedUserException(String message) {
        super(message);
    }
}
