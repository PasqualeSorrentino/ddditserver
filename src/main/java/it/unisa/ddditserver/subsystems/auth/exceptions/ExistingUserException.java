package it.unisa.ddditserver.subsystems.auth.exceptions;

/**
 * Exception thrown when attempting to create a user that already exists.
 *
 * This is a specific subtype of {@link AuthException} used to clearly indicate
 * that the provided user is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class ExistingUserException extends AuthException {
    public ExistingUserException(String message) {
        super(message);
    }
}