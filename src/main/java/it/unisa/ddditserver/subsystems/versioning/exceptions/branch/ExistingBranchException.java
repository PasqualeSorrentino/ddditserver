package it.unisa.ddditserver.subsystems.versioning.exceptions.branch;

/**
 * Exception thrown when attempting to create a branch that already exists.
 *
 * This is a specific subtype of {@link BranchException} used to clearly indicate
 * that the provided branch is already registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class ExistingBranchException extends BranchException {
    public ExistingBranchException(String message) {
        super(message);
    }
}
