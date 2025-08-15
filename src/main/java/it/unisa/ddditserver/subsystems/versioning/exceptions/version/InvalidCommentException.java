package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

/**
 * Exception thrown when comment validation fails due to invalid comment.
 *
 * This subclass of {@link VersionException} indicates that the provided comment is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidCommentException extends VersionException {
    public InvalidCommentException(String message) {
        super(message);
    }
}
