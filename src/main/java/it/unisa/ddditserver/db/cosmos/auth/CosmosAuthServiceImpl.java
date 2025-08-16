package it.unisa.ddditserver.db.cosmos.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.azure.cosmos.*;
import com.azure.cosmos.models.PartitionKey;
import it.unisa.ddditserver.subsystems.auth.dto.BlacklistedTokenDTO;
import it.unisa.ddditserver.subsystems.auth.exceptions.AuthException;
import it.unisa.ddditserver.db.cosmos.CosmosConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CosmosAuthServiceImpl implements CosmosAuthService {
    private final CosmosConfig config;
    private CosmosContainer blacklistContainer;

    @Autowired
    public CosmosAuthServiceImpl(CosmosConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        // Build client connection to Cosmos server
        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(config.getEndpoint())
                .key(config.getKey())
                .buildClient();

        // Get database reference
        CosmosDatabase database = cosmosClient.getDatabase(config.getDatabaseName());

        // Get container reference for token blacklist
        this.blacklistContainer = database.getContainer(config.getTokenBlacklistContainerName());
    }

    @Override
    public void blacklistToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            long tokenExpiryTimestamp = decodedJWT.getExpiresAt().getTime() / 1000;
            long currentTimestamp = System.currentTimeMillis() / 1000;

            int remainingTtl = (int) (tokenExpiryTimestamp - currentTimestamp);

            if (remainingTtl <= 0) return;

            BlacklistedTokenDTO blacklistedToken = new BlacklistedTokenDTO(token, token, remainingTtl);
            blacklistContainer.upsertItem(blacklistedToken);
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error blacklisting token");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            BlacklistedTokenDTO blacklistedTokenDTO = blacklistContainer.
                    readItem(token, new PartitionKey(token), BlacklistedTokenDTO.class).getItem();

            return blacklistedTokenDTO!= null;
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error checking blacklisted token");
        }
    }
}

