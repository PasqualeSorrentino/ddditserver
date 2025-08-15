package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

/**
 * Exception thrown when attempting to create a version that already exists.
 *
 * This is a specific subtype of {@link VersionException} used to clearly indicate
 * that the provided version is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class ExistingVersionException extends VersionException {
    public ExistingVersionException(String message) {
        super(message);
    }
}
