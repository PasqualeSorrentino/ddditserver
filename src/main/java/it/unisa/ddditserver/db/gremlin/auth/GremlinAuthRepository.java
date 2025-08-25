package it.unisa.ddditserver.db.gremlin.auth;

import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;

/**
 * Repository interface for managing authentication-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface GremlinAuthRepository {
    /**
     * Saves a new user vertex in the graph database.
     *
     * @param userDTO the UserDTO containing user information
     */
    void saveUser(UserDTO userDTO);

    /**
     * Finds a user vertex by username.
     *
     * @param userDTO the user to search for
     * @return UserDTO if found, null otherwise
     */
    UserDTO findByUser(UserDTO userDTO);

    /**
     * Checks if a user exits.
     *
     * @param userDTO the user to search for
     * @return true if user exists, false otherwise
     */
    boolean existsByUser(UserDTO userDTO);
}
