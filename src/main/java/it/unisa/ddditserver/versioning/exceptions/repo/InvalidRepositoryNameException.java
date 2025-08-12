package it.unisa.ddditserver.versioning.exceptions.repo;

/**
 * Exception thrown when repository creation fails due to invalid name.
 *
 * This subclass of {@link RepositoryException} indicates that the provided name for the repository is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public class InvalidRepositoryNameException extends RepositoryException {
    public InvalidRepositoryNameException(String message) {
        super(message);
    }
}
