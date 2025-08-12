package it.unisa.ddditserver.versioning.exceptions.repo;

import it.unisa.ddditserver.versioning.exceptions.VersionControlSystemException;

/**
 * Custom exception used to handle generic errors related to repositories operations.
 *
 * This subclass of {@link VersionControlSystemException} should be used when a repository-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public class RepositoryException extends VersionControlSystemException {
    public RepositoryException(String message) {
        super(message);
    }
}
