package it.unisa.ddditserver.subsystems.versioning.exceptions.version;

/**
 * Exception thrown when mesh validation fails due to invalid name of file or invalid extension.
 *
 * This subclass of {@link VersionException} indicates that the provided name for the mesh is malformed or the extension is incorrect.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidMeshException extends VersionException {
    public InvalidMeshException(String message) {
        super(message);
    }
}
