package it.unisa.ddditserver.subsystems.versioning.service.version;

import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import java.util.Map;

/**
 * Service interface for version-related operations in the versioning subsystem.
 * Provides methods to create different types of versions, retrieve version information,
 * and pull version data for authenticated users.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-16
 */
public interface VersionService {

    /**
     * Creates a new mesh version for a resource associated with the authenticated user.
     *
     * @param versionDTO the data transfer object containing the mesh version information
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data,
     *         such as the result of the mesh version creation operation
     */
    ResponseEntity<Map<String, String>> createVersion(VersionDTO versionDTO, String token);

    /**
     * Pulls the specified version data for the authenticated user.
     *
     * @param versionDTO the data transfer object representing the version to pull
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the version data
     */
    ResponseEntity<MultiValueMap<String, Object>> pullVersion(VersionDTO versionDTO, String token);

    /**
     * Retrieves detailed information about a specified version for the authenticated user.
     *
     * @param versionDTO the data transfer object representing the version whose information is requested
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with version details
     */
    ResponseEntity<Map<String, Object>> showVersionMetadata(VersionDTO versionDTO, String token);
}
