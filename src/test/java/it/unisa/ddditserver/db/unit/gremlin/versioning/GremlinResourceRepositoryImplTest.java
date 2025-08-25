package it.unisa.ddditserver.db.unit.gremlin.versioning;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.versioning.resource.GremlinResourceRepositoryImpl;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
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

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
class GremlinResourceRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinResourceRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinResourceRepositoryImpl(config);

        Field clientField = GremlinResourceRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: saveResource submits a query without throwing an exception
    void saveResourceSuccess() {
        ResourceDTO resource = new ResourceDTO("repo1", "res1");

        assertDoesNotThrow(() -> repository.saveResource(resource));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByRepository returns true when resource exists
    void existsByRepositoryReturnsTrue() {
        ResourceDTO resource = new ResourceDTO("repo1", "res1");

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByRepository(resource);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByRepository returns false when resource does not exist
    void existsByRepositoryReturnsFalse() {
        ResourceDTO resource = new ResourceDTO("repo1", "res1");

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByRepository(resource);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findResourcesByRepository returns a list of ResourceDTO
    void findResourcesByRepositoryReturnsList() {
        RepositoryDTO repo = new RepositoryDTO("repo1");

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("resourceName", List.of("res1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<ResourceDTO> resources = repository.findResourcesByRepository(repo);

        assertNotNull(resources);
        assertEquals(1, resources.size());
        assertEquals("res1", resources.get(0).getResourceName());
        assertEquals("repo1", resources.get(0).getRepositoryName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}

