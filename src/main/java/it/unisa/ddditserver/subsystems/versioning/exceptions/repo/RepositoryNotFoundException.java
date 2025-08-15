package it.unisa.ddditserver.subsystems.versioning.exceptions.repo;

/**
 * Exception thrown when a repository is not found after researching for it.
 *
 * This is a specific subtype of {@link RepositoryException} used to clearly indicate
 * that the provided repository is not registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class RepositoryNotFoundException extends RepositoryException {
    public RepositoryNotFoundException(String message) {
        super(message);
    }
}
