package it.unisa.ddditserver.db.gremlin.auth;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GremlinAuthServiceImpl implements GremlinAuthService {
    private final GremlinConfig config;
    private Cluster cluster;
    private Client client;

    public GremlinAuthServiceImpl(GremlinConfig config) {
        this.config = config;
    }

    /**
     * Create a connection to the graph database.
     */
    @PostConstruct
    public void init() {
        String endpoint = config.getEndpoint();
        // Remove protocol prefix (wss://)
        if (endpoint.startsWith("wss://")) {
            endpoint = endpoint.substring(6);
        }
        // Remove port and path after colon
        int colonIndex = endpoint.indexOf(':');
        if (colonIndex != -1) {
            endpoint = endpoint.substring(0, colonIndex);
        }
        // Remove trailing slash if present
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        // Build cluster connection to Gremlin server
        this.cluster = Cluster.build()
                .addContactPoint(endpoint)
                .port(443)
                .credentials(config.getUsername(), config.getKey())
                .enableSsl(true)
                .serializer(Serializers.GRAPHSON_V2D0)
                .create();

        this.client = cluster.connect();
    }

    @Override
    public void saveUser(UserDTO user) {
        // Use Gremlin query to add a vertex with label 'user' and properties
        String query = "g.addV('user')" +
                ".property('repoId', repoId)" +
                ".property('username', username)" +
                ".property('password', password)";

        // Prepare the query with parameter bindings to avoid injection and improve readability
        // The partition key configured in the Azure portal is repoId,
        // so for users who have not yet created a repository the chosen value is unassignedRepoId
        try {
            client.submit(query,
                    java.util.Map.of(
                            "repoId", "unassignedRepoId",
                            "username", user.getUsername(),
                            "password", user.getPassword()
                    )
            ).all().get();
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error saving user to Gremlin DB");
        }
    }

    @Override
    public UserDTO findByUsername(String username) {
        String query = "g.V().hasLabel('user').has('username', username).limit(1).valueMap()"; // Modifica qui
        try {
            ResultSet results = client.submit(query, java.util.Map.of("username", username));
            List<Result> list = results.all().get();
            if (list.isEmpty()) {
                return null;
            }

            Map<String, Object> vertexMap = (Map<String, Object>) list.get(0).getObject();

            String retrievedUsername = ((List<String>) vertexMap.get("username")).get(0);
            String retrievedPassword = ((List<String>) vertexMap.get("password")).get(0);

            UserDTO user = new UserDTO();
            user.setUsername(retrievedUsername);
            user.setPassword(retrievedPassword);
            return user;

        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error finding user by username in Gremlin DB");
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String query = "g.V().hasLabel('user').has('username', username).count()";
        try {
            ResultSet results = client.submit(query, java.util.Map.of("username", username));
            Long count = results.one().getLong();
            return count != null && count > 0;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error checking user existence in Gremlin DB");
        }
    }
}

