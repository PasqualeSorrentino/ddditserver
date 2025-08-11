package it.unisa.ddditserver.auth.exceptions;

/**
 * Exception thrown when attempting to register a user that already exists.
 * This is a specific subtype of {@link AuthException} used to clearly indicate
 * that the provided username is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public class ExistingUserException extends AuthException {
    public ExistingUserException(String message) {
        super(message);
    }
}