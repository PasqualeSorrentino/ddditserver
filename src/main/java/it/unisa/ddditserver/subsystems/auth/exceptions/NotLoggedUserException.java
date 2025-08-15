package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when a user tries to log out, but he is not logged.
 *
 * This exception extends {@link AuthException} and indicates
 * that the user is currently not authenticated.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class NotLoggedUserException extends AuthException {
    public NotLoggedUserException(String message) {
        super(message);
    }
}
