package it.unisa.ddditserver.db.cosmos.auth;

public interface CosmosAuthService {
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