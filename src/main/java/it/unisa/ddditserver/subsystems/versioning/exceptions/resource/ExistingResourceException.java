package it.unisa.ddditserver.subsystems.versioning.exceptions.resource;

/**
 * Exception thrown when attempting to create a resource that already exists.
 *
 * This is a specific subtype of {@link ResourceException} used to clearly indicate
 * that the provided resource is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class ExistingResourceException extends ResourceException {
    public ExistingResourceException(String message) {
        super(message);
    }
}
