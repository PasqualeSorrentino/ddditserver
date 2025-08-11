package it.unisa.ddditserver.db.gremlin.auth;

import it.unisa.ddditserver.auth.dto.UserDTO;

/**
 * Interface for manage graph database operations.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public interface GremlinAuthService {
    /**
     * Saves a new user vertex in the graph database.
     *
     * @param user the UserDTO containing user information
     */
    void saveUser(UserDTO user);

    /**
     * Finds a user vertex by username.
     *
     * @param username the username to search for
     * @return UserDTO if found, null otherwise
     */
    UserDTO findByUsername(String username);

    /**
     * Checks if a user with the specified username exists.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
}
