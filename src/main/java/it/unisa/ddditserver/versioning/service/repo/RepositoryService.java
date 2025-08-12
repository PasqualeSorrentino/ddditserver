package it.unisa.ddditserver.versioning.service.repo;

import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for repository-related operations.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public interface RepositoryService {

    /**
     * Creates a new repository linked to the authenticated user identified by the token.
     *
     * @param repository the repository to create
     * @param token the JWT token identifying the user
     * @return ResponseEntity with the result of the operation
     */
    ResponseEntity<Map<String, String>> createRepository(RepositoryDTO repository, String token);

    /**
     * Returns all repositories owned by the authenticated user identified by the token.
     *
     * @param token the JWT token identifying the user
     * @return ResponseEntity containing the list of repositories owned by the user
     */
    ResponseEntity<Map<String, Object>> listRepositoriesOwned(String token);

    /**
     * Returns all repositories to which the authenticated user identified by the token contributes.
     *
     * @param token the JWT token identifying the user
     * @return ResponseEntity containing the list of repositories the user contributes to
     */
    ResponseEntity<Map<String, Object>> listRepositoriesContributed(String token);
}
