package it.unisa.ddditserver.db.gremlin.versioning.repo;

import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.repo.RepositoryException;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class GremlinRepositoryRepositoryImpl implements GremlinRepositoryRepository {
    private final GremlinConfig config;
    private Client client;

    @Autowired
    public GremlinRepositoryRepositoryImpl(GremlinConfig config) {
        this.config = config;
    }

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
        Cluster cluster = Cluster.build()
                .addContactPoint(endpoint)
                .port(443)
                .credentials(config.getUsername(), config.getKey())
                .enableSsl(true)
                .serializer(Serializers.GRAPHSON_V2D0)
                .create();

        this.client = cluster.connect();
    }

    @Override
    public void saveRepository(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String username = userDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".as('u')" +
                ".addV('repository')" +
                ".property('repoId', repositoryName)" +
                ".property('repositoryName', repositoryName)" +
                ".as('r')" +
                ".addE('OWNS')" +
                ".from('u')" +
                ".to('r')";

        try {
            client.submit(query, Map.of(
                    "username", username,
                    "repositoryName", repositoryName));
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error during repository creation in Gremlin DB");
        }
    }

    @Override
    public boolean existsByRepository(RepositoryDTO repositoryDTO) {
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('repository')" +
                ".has('repositoryName', repositoryName)";
        try {
            List<Result> results = client.submit(query, Map.of("repositoryName", repositoryName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new VersionException("More than one repository found in Gremlin DB");
            }

            return true;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking repository existence in Gremlin DB");
        }
    }

    @Override
    public List<UserDTO> findContributorsByRepository(RepositoryDTO repositoryDTO) {
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('repository')" +
                ".has('repositoryName', repositoryName)" +
                ".union(in('CONTRIBUTES_TO'), in('OWNS'))" +
                ".dedup()" +
                ".valueMap()";
        try {
            List<Result> results = client.submit(query, Map.of("repositoryName", repositoryName)).all().get();
            List<UserDTO> contributors = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String username = props.get("username").get(0).toString();
                contributors.add(new UserDTO(username, null));
            }

            return contributors;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding contributors in Gremlin DB");
        }
    }

    @Override
    public boolean isContributor(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String username = userDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".out('CONTRIBUTES_TO')" +
                ".hasLabel('repository')" +
                ".has('repositoryName', repositoryName)";
        try {
            List<Result> results = client.submit(query, Map.of("username", username, "repositoryName", repositoryName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new VersionException("More than one contributor with the same username found in Gremlin DB");
            }

            return true;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking contributor status in Gremlin DB");
        }
    }

    @Override
    public boolean isOwner(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String username = userDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".out('OWNS')" +
                ".hasLabel('repository')" +
                ".has('repositoryName', repositoryName)";

        try {
            List<Result> results = client.submit(query, Map.of("username", username, "repositoryName", repositoryName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new VersionException("More than one owner found in Gremlin DB");
            }

            return true;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking owner status in Gremlin DB");
        }
    }

    @Override
    public void addContributor(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String username = userDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".as('u')" +
                ".V()" +
                ".hasLabel('repository')" +
                ".has('repositoryName', repositoryName)" +
                ".as('r')" +
                ".coalesce(" +
                    "__.V()" +
                    ".hasLabel('user')" +
                    ".has('username', username)" +
                    ".outE('CONTRIBUTES_TO')" +
                    ".where(inV().has('repositoryName', repositoryName))," +
                    "__.addE('CONTRIBUTES_TO')" +
                    ".from('u')" +
                    ".to('r')" +
                ")";

        try {
            client.submit(query, Map.of("username", username, "repositoryName", repositoryName));
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error adding contributor in Gremlin DB");
        }
    }

    @Override
    public List<RepositoryDTO> findOwnedRepositoriesByUser(UserDTO userDTO) {
        String username = userDTO.getUsername();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".out('OWNS')" +
                ".valueMap()";
        try {
            List<Result> results = client.submit(query, Map.of("username", username)).all().get();
            List<RepositoryDTO> repositories = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String repositoryName = props.get("repositoryName").get(0).toString();
                repositories.add(new RepositoryDTO(repositoryName));
            }

            return repositories;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding owned repositories in Gremlin DB");
        }
    }

    @Override
    public List<RepositoryDTO> findContributedRepositoriesByUser(UserDTO userDTO) {
        String username = userDTO.getUsername();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', username)" +
                ".out('CONTRIBUTES_TO')" +
                ".valueMap()";
        try {
            List<Result> results = client.submit(query, Map.of("username", username)).all().get();
            List<RepositoryDTO> repositories = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String repositoryName = props.get("repositoryName").get(0).toString();
                repositories.add(new RepositoryDTO(repositoryName));
            }

            return repositories;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding contributed repositories in Gremlin DB");
        }
    }
}