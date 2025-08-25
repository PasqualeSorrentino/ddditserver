package it.unisa.ddditserver.db.unit.gremlin.versioning;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.versioning.branch.GremlinBranchRepositoryImpl;
import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GremlinBranchRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinBranchRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinBranchRepositoryImpl(config);

        Field clientField = GremlinBranchRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: saveBranch submits a query without throwing an exception
    void saveBranchSuccess() {
        BranchDTO branch = new BranchDTO("repo1", "resource1", "branch1");

        assertDoesNotThrow(() -> repository.saveBranch(branch));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByResource returns true when branch exists
    void existsByResourceReturnsTrue() {
        BranchDTO branch = new BranchDTO("repo1", "resource1", "branch1");

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByResource(branch);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByResource returns false when branch does not exist
    void existsByResourceReturnsFalse() {
        BranchDTO branch = new BranchDTO("repo1", "resource1", "branch1");

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByResource(branch);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findBranchesByResource returns a list of branches
    void findBranchesByResourceReturnsList() {
        ResourceDTO resource = new ResourceDTO("repo1", "resource1");

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of(
                "branchName", List.of("branch1")
        ));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<BranchDTO> branches = repository.findBranchesByResource(resource);

        assertNotNull(branches);
        assertEquals(1, branches.size());
        assertEquals("branch1", branches.get(0).getBranchName());
        assertEquals("repo1", branches.get(0).getRepositoryName());
        assertEquals("resource1", branches.get(0).getResourceName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}

