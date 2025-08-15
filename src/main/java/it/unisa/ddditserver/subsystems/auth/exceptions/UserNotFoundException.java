package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when a user is not found after researching for it.
 *
 * This is a specific subtype of {@link AuthException} used to clearly indicate
 * that the provided user is not registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
