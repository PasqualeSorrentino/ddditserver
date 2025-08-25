package it.unisa.ddditserver.db.cosmos.auth;

/**
 * Repository interface for managing authentication-related operations
 * in a Cosmos DB.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-25
 */
public interface CosmosAuthRepository {
    /**
     * Adds a JWT token to the blacklist with a specified TTL (time to live).
     *
     * @param token the unique identifier of the token
     */
    void blacklistToken(String token);

    /**
     * Checks if a token is present in the blacklist.
     *
     * @param token the unique identifier of the token
     * @return true if the token is blacklisted (revoked), false otherwise
     */
    boolean isTokenBlacklisted(String token);
}