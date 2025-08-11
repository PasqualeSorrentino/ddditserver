package it.unisa.ddditserver.auth.exceptions;

/**
 * Exception thrown when the provided passwords do not match.
 *
 * This exception extends {@link RuntimeException} and indicates
 * that during the user log in operation the given password
 * differs from the one stored in the graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public class PasswordsMismatchException extends AuthException  {
    public PasswordsMismatchException(String message) {
        super(message);
    }
}
