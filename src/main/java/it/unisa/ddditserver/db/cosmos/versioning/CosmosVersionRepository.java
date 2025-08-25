package it.unisa.ddditserver.db.cosmos.versioning;

import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;

/**
 * Repository interface for managing version-related operations
 * in a Cosmos DB.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-25
 */
public interface CosmosVersionRepository {

    /**
     * Saves a version document in CosmosDB with version metadata.
     *
     * @param versionDTO the VersionDTO containing all version information
     */
    String saveVersion(VersionDTO versionDTO, String versionUrl);

    /**
     * Retrieves a version metadata from CosmosDB based on the provided document URL.
     *
     * @param cosmosDocumentUrl the CosmosDB URL that identifies the document
     * @return a {@link VersionDTO} containing the version metadata
     */
    VersionDTO findVersionByUrl(String cosmosDocumentUrl);

    /**
     * Retrieves the BLOB URL from the CosmosDB document based on the provided document URL.
     *
     * @param cosmosDocumentUrl the CosmosDB URL that identifies the document
     * @return a string containing the BLOB URL contained in the CosmosDB document
     */
    String getBlobUrlByUrl(String cosmosDocumentUrl);

    /**
     * Deletes the CosmosDB document based on the provided document URL.
     *
     * @param cosmosDocumentUrl the CosmosDB URL that identifies the document
     */
    void deleteVersionByUrl(String cosmosDocumentUrl);
}
