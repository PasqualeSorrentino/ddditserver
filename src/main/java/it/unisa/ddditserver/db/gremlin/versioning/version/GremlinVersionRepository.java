package it.unisa.ddditserver.db.gremlin.versioning.version;

import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import it.unisa.ddditserver.subsystems.versioning.service.version.NonClosingInputStreamResource;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

/**
 * Repository interface for managing version-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-13
 */
public interface GremlinVersionRepository {
    /**
     * Creates a new mesh version vertex in the graph database.
     *
     * @param versionDTO the VersionDTO containing mesh information
     * @param resourceType indicates a mesh version validation if true or a material version validation if false
     */
    void saveVersion(VersionDTO versionDTO, boolean resourceType);

    /**
     * Checks whether a version exists in the database.
     *
     * @param versionDTO the name of the version to check containing branch name, resource name and repository name
     * @return true if the version exists, false otherwise
     */
    boolean existsByVersion(VersionDTO versionDTO);

    /**
     * Retrieves information about a version vertex from the graph database.
     *
     * @param versionDTO the version to search for
     * @return the VersionDTO if found, or null if not found
     */
    VersionDTO findVersionByBranch(VersionDTO versionDTO);

    /**
     * Finds all versions in the specified branch.
     *
     * @param branchDTO the branch to search for
     * @return a list of {@link VersionDTO} representing versions associated to the branch
     */
    List<VersionDTO> findVersionsByBranch(BranchDTO branchDTO);

    /**
     * Retrieves the mesh file content or material files content associated with a specific version node.
     *
     * @param versionDTO containing the criteria to find the mesh file or the material files
     * @return the mesh file with associated content-type data if found, the material files with associated content-type data if found, or null if not found
     */
    List<Pair<NonClosingInputStreamResource, String>> getFile(VersionDTO versionDTO);
}