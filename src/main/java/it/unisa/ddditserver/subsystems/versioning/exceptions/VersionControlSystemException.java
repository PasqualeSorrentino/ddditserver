package it.unisa.ddditserver.subsystems.versioning.exceptions;

/**
 * Custom exception used to handle generic version control system errors.
 *
 * This exception should be used when a version control system-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class VersionControlSystemException extends RuntimeException {
    public VersionControlSystemException(String message) {
        super(message);
    }
}
