package it.unisa.ddditserver.db.gremlin.versioning.resource;

import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import java.util.List;

/**
 * Repository interface for managing resource-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-13
 */
public interface GremlinResourceRepository {
    /**
     * Saves a new resource vertex in the graph database.
     *
     * @param resourceDTO the ResourceDTO containing resource information
     */
    void saveResource(ResourceDTO resourceDTO);

    /**
     * Checks whether a resource exists in the database.
     *
     * @param resourceDTO the resource to search for
     * @return true if resource exists, false otherwise
     */
    boolean existsByRepository(ResourceDTO resourceDTO);

    /**
     * Finds all resources in the specified repository.
     *
     * @param repositoryDTO the repository to search for
     * @return a list of {@link ResourceDTO} representing resources contained in the repository
     */
    List<ResourceDTO> findResourcesByRepository(RepositoryDTO repositoryDTO);
}
