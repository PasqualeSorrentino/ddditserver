package it.unisa.ddditserver.subsystems.invitation.exceptions;

/**
 * Exception thrown when attempting to invite a user who has already
 * received an invitation.
 *
 * This is a specific subtype of {@link InvitationException} used to clearly indicate
 * that a user is trying to send a replica of an invitation to the same user who already received it, accepted or not.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class AlreadyInvitedException extends InvitationException {
    public AlreadyInvitedException(String message) {
        super(message);
    }
}
