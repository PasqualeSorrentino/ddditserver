package it.unisa.ddditserver.db.cosmos.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.azure.cosmos.*;
import it.unisa.ddditserver.auth.dto.BlacklistedTokenDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.db.cosmos.CosmosConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class CosmosAuthServiceImpl implements CosmosAuthService {
    private final CosmosConfig config;
    private CosmosClient cosmosClient;
    private CosmosContainer blacklistContainer;

    public CosmosAuthServiceImpl(CosmosConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        // Initialize Cosmos DB client
        this.cosmosClient = new CosmosClientBuilder()
                .endpoint(config.getEndpoint())
                .key(config.getKey())
                .buildClient();

        // Get database reference
        CosmosDatabase database = cosmosClient.getDatabase(config.getDatabaseName());

        // Get container reference for token blacklist
        this.blacklistContainer = database.getContainer(config.getTokenBlacklistContainerName());
    }

    @Override
    public void blacklistToken(String tokenId) {
        try {
            DecodedJWT decodedJWT = JWT.decode(tokenId);
            long tokenExpiryTimestamp = decodedJWT.getExpiresAt().getTime() / 1000;
            long currentTimestamp = System.currentTimeMillis() / 1000;

            int remainingTtl = (int) (tokenExpiryTimestamp - currentTimestamp);

            if (remainingTtl <= 0) {
                throw new AuthException("Token already expired, no need to blacklist");
            }

            BlacklistedTokenDTO blacklistedToken = new BlacklistedTokenDTO(tokenId, tokenId, remainingTtl);

            blacklistContainer.upsertItem(blacklistedToken);

        } catch (Exception e) {
            throw new AuthException("Error blacklisting token");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String tokenId) {
        try {
            BlacklistedTokenDTO token = blacklistContainer.readItem(tokenId, new com.azure.cosmos.models.PartitionKey(tokenId), BlacklistedTokenDTO.class).getItem();
            return token != null;
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            throw new AuthException("Error checking blacklisted token");
        }
    }
}

