package it.unisa.ddditserver.auth.exceptions;

import lombok.Getter;
import java.util.Collections;
import java.util.List;

/**
 * Custom exception used to handle generic authentication errors.
 * This exception should be used when an authentication-related error occurs
 * that does not fit into any of the more specific child exception types.
 * It can also store a list of underlying causes for more detailed error tracking.
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