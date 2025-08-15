package it.unisa.ddditserver.subsystems.versioning.exceptions.resource;

/**
 * Exception thrown when resource name validation fails due to invalid name.
 *
 * This subclass of {@link ResourceException} indicates that the provided name for the resource is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidResourceNameException extends ResourceException {
    public InvalidResourceNameException(String message) {
        super(message);
    }
}
