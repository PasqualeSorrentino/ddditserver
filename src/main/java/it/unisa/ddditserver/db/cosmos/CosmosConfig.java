package it.unisa.ddditserver.db.cosmos;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CosmosConfig {
    @Value("${COSMOS_SQL_ENDPOINT}")
    private String endpoint;

    @Value("${COSMOS_SQL_KEY}")
    private String key;

    @Value("${COSMOS_SQL_DATABASE}")
    private String databaseName;

    @Value("${COSMOS_SQL_CONTAINER}")
    private String containerName;
}