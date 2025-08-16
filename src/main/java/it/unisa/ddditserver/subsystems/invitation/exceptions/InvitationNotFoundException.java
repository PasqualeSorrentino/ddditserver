package it.unisa.ddditserver.subsystems.invitation.exceptions;

/**
 * Exception thrown when an invitation is not found after researching for it.
 *
 * This is a specific subtype of {@link InvitationException} used to clearly indicate
 * that the provided invitation is not registered in the system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvitationNotFoundException extends InvitationException {
    public InvitationNotFoundException(String message) {
        super(message);
    }
}
