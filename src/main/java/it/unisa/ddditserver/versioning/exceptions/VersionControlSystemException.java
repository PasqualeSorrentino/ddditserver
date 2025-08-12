package it.unisa.ddditserver.versioning.exceptions;

/**
 * Custom exception used to handle generic version control system related errors.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public class VersionControlSystemException extends RuntimeException {
    public VersionControlSystemException(String message) {
        super(message);
    }
}
