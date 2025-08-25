package it.unisa.ddditserver.db.gremlin.versioning.branch;

import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import java.util.List;

/**
 * Repository interface for managing branch-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-13
 */
public interface GremlinBranchRepository {
    /**
     * Saves a new branch vertex in the graph database.
     *
     * @param branchDTO the BranchDTO containing branch information
     */
    void saveBranch(BranchDTO branchDTO);

    /**
     * Checks whether a branch exists in the database.
     *
     * @param branchDTO the branch to search for
     * @return true if branch exists, false otherwise
     */
    boolean existsByResource(BranchDTO branchDTO);

    /**
     * Finds all branches for a specified resource.
     *
     * @param resourceDTO the resource to search for in a specific repository
     * @return a list of {@link BranchDTO} representing branches of the resource
     */
    List<BranchDTO> findBranchesByResource(ResourceDTO resourceDTO);
}
