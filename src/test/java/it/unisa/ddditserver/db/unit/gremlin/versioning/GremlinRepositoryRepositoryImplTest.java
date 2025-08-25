package it.unisa.ddditserver.db.unit.gremlin.versioning;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.versioning.repo.GremlinRepositoryRepositoryImpl;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class GremlinRepositoryRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinRepositoryRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinRepositoryRepositoryImpl(config);

        Field clientField = GremlinRepositoryRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: saveRepository submits a query without throwing an exception
    void saveRepositorySuccess() {
        UserDTO user = new UserDTO("user1", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        assertDoesNotThrow(() -> repository.saveRepository(repo, user));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByRepository returns true when repository exists
    void existsByRepositoryReturnsTrue() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByRepository(repo);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByRepository returns false when repository does not exist
    void existsByRepositoryReturnsFalse() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByRepository(repo);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findContributorsByRepository returns a list of UserDTO
    void findContributorsByRepositoryReturnsList() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("username", List.of("user1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<UserDTO> contributors = repository.findContributorsByRepository(repo);

        assertNotNull(contributors);
        assertEquals(1, contributors.size());
        assertEquals("user1", contributors.get(0).getUsername());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isContributor returns true when user is a contributor
    void isContributorReturnsTrue() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean result = repository.isContributor(repo, user);

        assertTrue(result);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isContributor returns false when user is not a contributor
    void isContributorReturnsFalse() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");
        UserDTO user = new UserDTO("user1", null);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean result = repository.isContributor(repo, user);

        assertFalse(result);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isOwner returns true when user is the owner
    void isOwnerReturnsTrue() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean result = repository.isOwner(repo, user);

        assertTrue(result);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isOwner returns false when user is not the owner
    void isOwnerReturnsFalse() throws Exception {
        RepositoryDTO repo = new RepositoryDTO("repo1");
        UserDTO user = new UserDTO("user1", null);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean result = repository.isOwner(repo, user);

        assertFalse(result);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: addContributor submits a query without throwing an exception
    void addContributorSuccess() {
        RepositoryDTO repo = new RepositoryDTO("repo1");
        UserDTO user = new UserDTO("user1", null);

        assertDoesNotThrow(() -> repository.addContributor(repo, user));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findOwnedRepositoriesByUser returns a list of RepositoryDTO
    void findOwnedRepositoriesByUserReturnsList() throws Exception {
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("repositoryName", List.of("repo1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<RepositoryDTO> repos = repository.findOwnedRepositoriesByUser(user);

        assertNotNull(repos);
        assertEquals(1, repos.size());
        assertEquals("repo1", repos.get(0).getRepositoryName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findContributedRepositoriesByUser returns a list of RepositoryDTO
    void findContributedRepositoriesByUserReturnsList() throws Exception {
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("repositoryName", List.of("repo1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<RepositoryDTO> repos = repository.findContributedRepositoriesByUser(user);

        assertNotNull(repos);
        assertEquals(1, repos.size());
        assertEquals("repo1", repos.get(0).getRepositoryName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}
