package it.unisa.ddditserver.versioning.exceptions.repo;

/**
 * Exception thrown when attempting to create a repository that already exists.
 *
 * This is a specific subtype of {@link RepositoryException} used to clearly indicate
 * that the provided name is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public class ExistingRepositoryException extends RepositoryException {
    public ExistingRepositoryException(String message) {
        super(message);
    }
}
