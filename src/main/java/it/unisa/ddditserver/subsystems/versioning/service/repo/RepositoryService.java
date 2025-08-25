package it.unisa.ddditserver.subsystems.versioning.service.repo;

import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for repository-related operations in the versioning subsystem.
 * Provides methods to create repositories and retrieve repositories owned or contributed
 * by authenticated users.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public interface RepositoryService {

    /**
     * Creates a new repository associated with the authenticated user identified by the provided token.
     *
     * @param repositoryDTO the data transfer object containing the repository information to be created
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data, such as
     *         the result of the repository creation operation
     */
    ResponseEntity<Map<String, String>> createRepository(RepositoryDTO repositoryDTO, String token);

    /**
     * Retrieves all repositories owned by the authenticated user identified by the provided token.
     *
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the list of repositories owned by the user
     */
    ResponseEntity<Map<String, Object>> listRepositoriesOwned(String token);

    /**
     * Retrieves all repositories to which the authenticated user contributes.
     *
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the list of repositories the user contributes to
     */
    ResponseEntity<Map<String, Object>> listRepositoriesContributed(String token);
}
