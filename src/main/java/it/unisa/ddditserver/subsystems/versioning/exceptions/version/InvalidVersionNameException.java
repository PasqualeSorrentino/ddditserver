package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

/**
 * Exception thrown when version name validation fails due to invalid name.
 *
 * This subclass of {@link VersionException} indicates that the provided name for the version is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidVersionNameException extends VersionException {
    public InvalidVersionNameException(String message) {
        super(message);
    }
}
