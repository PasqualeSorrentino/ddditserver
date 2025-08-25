package it.unisa.ddditserver.validators.auth.JWT;

/**
 * Interface for validating JWT tokens.
 *
 * @author Angelo Antonio Prisco
 * @version 1.3
 * @since 2025-08-13
 */
public interface JWTokenValidator {
    /**
     * Validates the token.
     *
     * @param token the token to validate
     * @return a string containing the username of the user that sent it, null if the token is not valid
     */
    String isTokenValid(String token);
}
