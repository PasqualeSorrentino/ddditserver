package it.unisa.ddditserver.subsystems.versioning.exceptions.branch;

/**
 * Exception thrown when branch name validation fails due to invalid name.
 *
 * This subclass of {@link BranchException} indicates that the provided name for the branch is malformed.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvalidBranchNameException extends BranchException {
    public InvalidBranchNameException(String message) {
        super(message);
    }
}
