package it.unisa.ddditserver.subsystems.versioning.exceptions.branch;

import it.unisa.ddditserver.subsystems.versioning.exceptions.VersionControlSystemException;

/**
 * Custom exception used to handle generic errors related to branches operations.
 *
 * This subclass of {@link VersionControlSystemException} should be used when a branch-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class BranchException extends VersionControlSystemException {
    public BranchException(String message) {
        super(message);
    }
}
