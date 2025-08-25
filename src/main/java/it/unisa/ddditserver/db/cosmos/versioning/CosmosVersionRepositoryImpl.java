package it.unisa.ddditserver.db.cosmos.versioning;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import it.unisa.ddditserver.db.cosmos.CosmosConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.version.CosmosVersionDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

@Repository
public class CosmosVersionRepositoryImpl implements CosmosVersionRepository {
    private final CosmosConfig config;
    private CosmosContainer container;

    @Autowired
    public CosmosVersionRepositoryImpl(CosmosConfig config) {
        this.config = config;
    }

    private String getPartitionKey(String query) {
        // If it is necessary use a RuntimeException for more detailed debug
        return Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith("partitionKey="))
                .map(param -> param.split("=")[1])
                .findFirst()
                .orElseThrow(() -> new VersionException("PartitionKey non found in CosmosDB document URL"));
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

        // Get container reference for versions metadata
        this.container = database.getContainer(config.getVersionsContainerName());
    }

    @Override
    public String saveVersion(VersionDTO versionDTO, String blobUrl) {
        CosmosVersionDTO cosmosVersion = new CosmosVersionDTO(
                UUID.randomUUID().toString(),
                versionDTO.getResourceName(),
                versionDTO.getResourceName(),
                versionDTO.getVersionName(),
                versionDTO.getUsername(),
                versionDTO.getPushedAt(),
                versionDTO.getComment(),
                versionDTO.getTags(),
                blobUrl
        );

        try {
            container.createItem(cosmosVersion, new PartitionKey(cosmosVersion.getResourceName()), new CosmosItemRequestOptions());

            // Should be added a new env variable with CosmosDB name and an env variable with username both on GitHub and Azure VM
            return String.format(
                    "https://%s.documents.azure.com/dbs/%s/colls/%s/docs/%s?partitionKey=%s",
                    "se4ai-aap-documents",
                    "metadata",
                    "versions",
                    cosmosVersion.getId(),
                    cosmosVersion.getResourceName()
            );
        } catch (CosmosException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error saving version document in CosmosDB");
        }
    }

    @Override
    public VersionDTO findVersionByUrl(String cosmosDocumentUrl) {
        try {
            URI uri = new URI(cosmosDocumentUrl);
            String[] pathSegments = uri.getPath().split("/");
            String versionId = pathSegments[pathSegments.length - 1];


            String query = uri.getQuery();
            CosmosVersionDTO cosmosVersion = container.readItem(versionId, new PartitionKey(getPartitionKey(query)), CosmosVersionDTO.class).getItem();

            if (cosmosVersion == null) {
                throw new VersionException("CosmosDB document version not found in CosmosDB");
            }

            // Some fields are null because in this case we are interested only to retrieve metadata
            return new VersionDTO(
                    null, null,
                    null, cosmosVersion.getVersionName(),
                    cosmosVersion.getUsername(), cosmosVersion.getPushedAt(),
                    cosmosVersion.getComment(), cosmosVersion.getTags(),
                    null, null
            );
        } catch (CosmosException | URISyntaxException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error retrieving version document from CosmosDB via URL");
        }
    }

    @Override
    public String getBlobUrlByUrl(String cosmosDocumentUrl) {
        try {
            URI uri = new URI(cosmosDocumentUrl);
            String[] pathSegments = uri.getPath().split("/");
            String versionId = pathSegments[pathSegments.length - 1];

            String query = uri.getQuery();
            CosmosVersionDTO cosmosVersion = container.readItem(versionId, new PartitionKey(getPartitionKey(query)), CosmosVersionDTO.class).getItem();

            if (cosmosVersion == null) {
                throw new VersionException("CosmosDB document version not found in CosmosDB");
            }

            return cosmosVersion.getBlobUrl();
        } catch (CosmosException | URISyntaxException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error retrieving version document from CosmosDB via URL");
        }
    }

    @Override
    public void deleteVersionByUrl(String cosmosDocumentUrl) {
        try {
            URI uri = new URI(cosmosDocumentUrl);
            String[] pathSegments = uri.getPath().split("/");
            String versionId = pathSegments[pathSegments.length - 1];

            String query = uri.getQuery();
            container.deleteItem(versionId, new PartitionKey(getPartitionKey(query)), new CosmosItemRequestOptions());
        } catch (CosmosException | URISyntaxException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error deleting version document in CosmosDB");
        }
    }
}