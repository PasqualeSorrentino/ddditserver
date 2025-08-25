package it.unisa.ddditserver.db.gremlin.versioning.repo;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import java.util.List;

/**
 * Repository interface for managing repository-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface GremlinRepositoryRepository {

    /**
     * Creates a new repository vertex in the graph database and associates it
     * with an existing user as the owner.
     *
     * @param repositoryDTO the RepositoryDTO containing repository information
     * @param userDTO the UserDTO containing user who will own the repository
     */
    void saveRepository(RepositoryDTO repositoryDTO, UserDTO userDTO);

    /**
     * Checks whether a repository exists in the database.
     *
     * @param repositoryDTO the repository to search for
     * @return true if the repository exists, false otherwise
     */
    boolean existsByRepository(RepositoryDTO repositoryDTO);

    /**
     * Retrieves the list of contributors for a given repository.
     *
     * @param repositoryDTO the repository to search for
     * @return a list of {@link UserDTO} objects representing the contributors
     */
    List<UserDTO> findContributorsByRepository(RepositoryDTO repositoryDTO);

    /**
     * Checks if the given user is a contributor to the specified repository.
     *
     * @param repositoryDTO the repository to search for
     * @param userDTO the user to verify
     * @return true if the user is a contributor, false otherwise
     */
    boolean isContributor(RepositoryDTO repositoryDTO, UserDTO userDTO);

    /**
     * Checks if the given user is the owner of the specified repository.
     *
     * @param repositoryDTO the repository to search for
     * @param userDTO the user to verify
     * @return true if the user is the owner, false otherwise
     */
    boolean isOwner(RepositoryDTO repositoryDTO, UserDTO userDTO);

    /**
     * Adds an existing user as a contributor to an existing repository creating an edge between the two nodes.
     *
     * @param repositoryDTO the repository to update
     * @param userDTO the user to add as a contributor
     */
    void addContributor(RepositoryDTO repositoryDTO, UserDTO userDTO);

    /**
     * Finds all repositories where the user is the owner.
     *
     * @param userDTO the user to search owned repositories for
     * @return a list of {@link RepositoryDTO} representing repositories owned by the user
     */
    List<RepositoryDTO> findOwnedRepositoriesByUser(UserDTO userDTO);

    /**
     * Finds all repositories where the user is a contributor.
     *
     * @param userDTO the user to search contributed repositories for
     * @return a list of {@link RepositoryDTO} representing repositories the user contributes to
     */
    List<RepositoryDTO> findContributedRepositoriesByUser(UserDTO userDTO);
}