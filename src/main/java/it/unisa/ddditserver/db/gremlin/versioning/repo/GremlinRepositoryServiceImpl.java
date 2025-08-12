package it.unisa.ddditserver.db.gremlin.versioning.repo;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.versioning.exceptions.repo.RepositoryException;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class GremlinRepositoryServiceImpl implements GremlinRepositoryService {

    private final GremlinConfig config;
    private Cluster cluster;
    private Client client;

    public GremlinRepositoryServiceImpl(GremlinConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        String endpoint = config.getEndpoint();
        if (endpoint.startsWith("wss://")) {
            endpoint = endpoint.substring(6);
        }
        int colonIndex = endpoint.indexOf(':');
        if (colonIndex != -1) {
            endpoint = endpoint.substring(0, colonIndex);
        }
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

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
    public void saveRepository(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String query = ""
                + "g.V().hasLabel('user').has('username', username).as('u')"
                + ".property('repoId', repositoryName)"
                + ".V().hasLabel('repository').has('name', repositoryName)"
                + ".fold().coalesce(unfold(), addV('repository')"
                + ".property('repoId', repositoryName)"
                + ".property('name', repositoryName))"
                + ".as('r')"
                + ".addE('OWNS').from('u').to('r')";

        try {
            client.submit(query, Map.of(
                    "username", userDTO.getUsername(),
                    "repositoryName", repositoryDTO.getRepositoryName()
            )).all().get();
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error saving repository to Gremlin DB");
        }
    }

    @Override
    public boolean existsByRepositoryName(String repositoryName) {
        String query = "g.V().hasLabel('repository').has('name', repositoryName).count()";
        try {
            ResultSet results = client.submit(query, Map.of("repositoryName", repositoryName));
            Long count = results.one().getLong();
            return count != null && count > 0;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking repository existence in Gremlin DB");
        }
    }

    @Override
    public List<UserDTO> findContributorsByRepositoryName(String repositoryName) {
        String query = "g.V().hasLabel('repository').has('name', repositoryName)"
                + ".union(in('CONTRIBUTES_TO'), in('OWNS'))"
                + ".dedup()"
                + ".valueMap()";
        try {
            ResultSet results = client.submit(query, Map.of("repositoryName", repositoryName));
            List<Result> list = results.all().get();

            List<UserDTO> contributors = new ArrayList<>();
            for (Result r : list) {
                Map<String, Object> map = (Map<String, Object>) r.getObject();
                UserDTO user = new UserDTO();
                user.setUsername(((List<String>) map.get("username")).get(0));
                user.setPassword(((List<String>) map.get("password")).get(0));
                contributors.add(user);
            }
            return contributors;
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding contributors in Gremlin DB");
        }
    }


    @Override
    public boolean isContributor(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String query = "g.V().hasLabel('user').has('username', username)"
                + ".out('CONTRIBUTES_TO').hasLabel('repository').has('name', repositoryName).count()";
        try {
            ResultSet results = client.submit(query, Map.of(
                    "username", userDTO.getUsername(),
                    "repositoryName", repositoryDTO.getRepositoryName()
            ));
            Long count = results.one().getLong();
            return count != null && count > 0;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking contributor status in Gremlin DB");
        }
    }

    @Override
    public boolean isOwner(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String query = "g.V().hasLabel('user').has('username', username)"
                + ".out('OWNS').hasLabel('repository').has('name', repositoryName).count()";
        try {
            ResultSet results = client.submit(query, Map.of(
                    "username", userDTO.getUsername(),
                    "repositoryName", repositoryDTO.getRepositoryName()
            ));
            Long count = results.one().getLong();
            return count != null && count > 0;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error checking owner status in Gremlin DB");
        }
    }

    @Override
    public void addContributor(RepositoryDTO repositoryDTO, UserDTO userDTO) {
        String query = "g.V().hasLabel('user').has('username', username).as('u')"
                + ".V().hasLabel('repository').has('name', repositoryName).as('r')"
                + ".addE('CONTRIBUTES_TO').from('u').to('r')";

        try {
            client.submit(query, Map.of(
                    "username", userDTO.getUsername(),
                    "repositoryName", repositoryDTO.getRepositoryName()
            )).all().get();
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error adding contributor in Gremlin DB");
        }
    }

    @Override
    public List<RepositoryDTO> findOwnedRepositoriesByUser(UserDTO userDTO) {
        String query = "g.V().hasLabel('user').has('username', username)"
                + ".out('OWNS')"
                + ".valueMap()";

        try {
            ResultSet results = client.submit(query, Map.of("username", userDTO.getUsername()));
            List<Result> list = results.all().get();

            List<RepositoryDTO> repositories = new ArrayList<>();
            for (Result r : list) {
                Map<String, Object> map = (Map<String, Object>) r.getObject();
                RepositoryDTO repo = new RepositoryDTO();
                repo.setRepositoryName(((List<String>) map.get("name")).get(0));
                repositories.add(repo);
            }
            return repositories;
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding owned repositories in Gremlin DB");
        }
    }

    @Override
    public List<RepositoryDTO> findContributedRepositoriesByUser(UserDTO userDTO) {
        String query = "g.V().hasLabel('user').has('username', username)"
                + ".out('CONTRIBUTES_TO')"
                + ".valueMap()";

        try {
            ResultSet results = client.submit(query, Map.of("username", userDTO.getUsername()));
            List<Result> list = results.all().get();

            List<RepositoryDTO> repositories = new ArrayList<>();
            for (Result r : list) {
                Map<String, Object> map = (Map<String, Object>) r.getObject();
                RepositoryDTO repo = new RepositoryDTO();
                repo.setRepositoryName(((List<String>) map.get("name")).get(0));
                repositories.add(repo);
            }
            return repositories;
        } catch (InterruptedException | ExecutionException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new RepositoryException("Error finding contributed repositories in Gremlin DB");
        }
    }
}

