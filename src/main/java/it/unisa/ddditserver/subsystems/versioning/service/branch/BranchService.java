package it.unisa.ddditserver.subsystems.versioning.service.branch;

import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for branch-related operations in the versioning subsystem.
 * Provides methods to create branches and retrieve branches associated with resources
 * for authenticated users.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public interface BranchService {

    /**
     * Creates a new branch associated with the authenticated user identified by the provided token.
     *
     * @param branchDTO the data transfer object containing the branch information to be created
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data, such as
     *         the result of the branch creation operation
     */
    ResponseEntity<Map<String, String>> createBranch(BranchDTO branchDTO, String token);

    /**
     * Retrieves all branches associated with a given resource and owned by the authenticated user.
     *
     * @param resourceDTO the data transfer object representing the resource to search branches for
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the list of branches related to the resource
     */
    ResponseEntity<Map<String, Object>> listBranchesByResource(ResourceDTO resourceDTO, String token);
}
