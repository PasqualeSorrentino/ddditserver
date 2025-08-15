package it.unisa.ddditserver.subsystems.versioning.exceptions.repo;

/**
 * Exception thrown when repository name validation fails due to invalid name.
 *
 * This subclass of {@link RepositoryException} indicates that the provided name for the repository is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidRepositoryNameException extends RepositoryException {
    public InvalidRepositoryNameException(String message) {
        super(message);
    }
}
