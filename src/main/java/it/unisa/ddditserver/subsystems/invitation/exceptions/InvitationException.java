package it.unisa.ddditserver.subsystems.invitation.exceptions;

/**
 * Custom exception used to handle generic invitation errors.
 *
 * This exception should be used when an invitation-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public class InvitationException extends RuntimeException {
    public InvitationException(String message) {
        super(message);
    }
}
