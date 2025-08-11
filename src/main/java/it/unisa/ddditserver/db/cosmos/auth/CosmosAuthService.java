package it.unisa.ddditserver.db.cosmos.auth;

public interface CosmosAuthService {
    /**
     * Adds a JWT token to the blacklist with a specified TTL (time to live).
     *
     * @param tokenId the unique identifier of the token (e.g., the 'jti' claim)
     */
    void blacklistToken(String tokenId);

    /**
     * Checks if a token is present in the blacklist (i.e., revoked).
     *
     * @param tokenId the unique identifier of the token (e.g., the 'jti' claim)
     * @return true if the token is blacklisted (revoked), false otherwise
     */
    boolean isTokenBlacklisted(String tokenId);
}