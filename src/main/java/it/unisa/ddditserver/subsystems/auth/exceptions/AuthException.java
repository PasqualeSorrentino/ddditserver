package it.unisa.ddditserver.subsystems.auth.exceptions;

import lombok.Getter;

/**
 * Custom exception used to handle generic authentication errors.
 *
 * This exception should be used when an authentication-related error occurs
 * that does not fit into any of the more specific child exception types.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
@Getter
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}