package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

import it.unisa.ddditserver.subsystems.versioning.exceptions.VersionControlSystemException;

/**
 * Custom exception used to handle generic errors related to versions operations.
 *
 * This subclass of {@link VersionControlSystemException} should be used when a version-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class VersionException extends VersionControlSystemException {
    public VersionException(String message) {
        super(message);
    }
}
