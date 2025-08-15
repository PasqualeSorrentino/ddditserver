package it.unisa.ddditserver.subsystems.versioning.exceptions.branch;

/**
 * Exception thrown when a branch is not found after researching for it.
 *
 * This is a specific subtype of {@link BranchException} used to clearly indicate
 * that the provided branch is not registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class BranchNotFoundException extends BranchException {
    public BranchNotFoundException(String message) {
        super(message);
    }
}
