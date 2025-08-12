package it.unisa.ddditserver.versioning.controller.repo;

import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller interface for repository endpoints.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public interface RepositoryController {

    /**
     * Creates a new repository for the authenticated user.
     *
     * @param repository the repository data to create
     * @param request the HTTP servlet request containing authorization headers
     * @return a ResponseEntity with the result of the operation
     */
    ResponseEntity<?> createRepository(@RequestBody RepositoryDTO repository, HttpServletRequest request);

    /**
     * Lists all repositories owned by the authenticated user.
     *
     * @param request the HTTP servlet request containing authorization headers
     * @return a ResponseEntity containing the list of owned repositories
     */
    ResponseEntity<?> listOwnedRepositories(HttpServletRequest request);

    /**
     * Lists all repositories to which the authenticated user contributes.
     *
     * @param request the HTTP servlet request containing authorization headers
     * @return a ResponseEntity containing the list of contributed repositories
     */
    ResponseEntity<?> listContributedRepositories(HttpServletRequest request);
}

