package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

/**
 * Exception thrown when a version is not found after researching for it.
 *
 * This is a specific subtype of {@link VersionException} used to clearly indicate
 * that the provided version is not registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class VersionNotFoundException extends VersionException {
    public VersionNotFoundException(String message) {
        super(message);
    }
}
