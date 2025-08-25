package it.unisa.ddditserver.subsystems.versioning.service.resource;

import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for resource-related operations in the versioning subsystem.
 * Provides methods to create resources, list resources by repository,
 * and retrieve the version tree of a resource for authenticated users.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-14
 */
public interface ResourceService {

    /**
     * Creates a new resource associated with the authenticated user identified by the provided token.
     *
     * @param resourceDTO the data transfer object containing the resource information to be created
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data,
     *         such as the result of the resource creation operation
     */
    ResponseEntity<Map<String, String>> createResource(ResourceDTO resourceDTO, String token);

    /**
     * Retrieves all resources contained in the specified repository for the authenticated user.
     *
     * @param repositoryDTO the data transfer object representing the repository to search resources for
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the list of resources in the repository
     */
    ResponseEntity<Map<String, Object>> listResourcesByRepository(RepositoryDTO repositoryDTO, String token);

    /**
     * Retrieves the version tree of the specified resource for the authenticated user.
     *
     * @param resourceDTO the data transfer object representing the resource whose version tree is requested
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the version tree of the specified resource
     */
    ResponseEntity<Map<String, Object>> showVersionTree(ResourceDTO resourceDTO, String token);
}
