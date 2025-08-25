package it.unisa.ddditserver.db.unit.gremlin.versioning;

import it.unisa.ddditserver.db.blobstorage.versioning.BlobStorageVersionRepository;
import it.unisa.ddditserver.db.cosmos.versioning.CosmosVersionRepository;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.versioning.version.GremlinVersionRepositoryImpl;
import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
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
class GremlinVersionRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private CosmosVersionRepository cosmosService;

    @Mock
    private BlobStorageVersionRepository blobStorageService;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinVersionRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinVersionRepositoryImpl(config, cosmosService, blobStorageService);

        Field clientField = GremlinVersionRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: existsByVersion returns true when version exists
    void existsByVersionReturnTrue() throws Exception {
        VersionDTO version = new VersionDTO("repo1", "res1", "branch1", "v1", null, null, null, null, null, null);

        Result mockResult = mock(Result.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByVersion(version);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByVersion returns false when version does not exist
    void existsByVersionReturnFalse() throws Exception {
        VersionDTO version = new VersionDTO("repo1", "res1", "branch1", "v1", null, null, null, null, null, null);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByVersion(version);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findVersionByBranch returns VersionDTO from Cosmos
    void findVersionByBranchSuccess() throws Exception {
        VersionDTO version = new VersionDTO("repo1", "res1", "branch1", "v1", null, null, null, null, null, null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("cosmosDocumentUrl", List.of("url1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        VersionDTO returned = mock(VersionDTO.class);
        when(cosmosService.findVersionByUrl("url1")).thenReturn(returned);

        VersionDTO result = repository.findVersionByBranch(version);

        assertEquals(returned, result);
        verify(client, times(1)).submit(anyString(), any(Map.class));
        verify(cosmosService, times(1)).findVersionByUrl("url1");
    }

    @Test
    // Happy path: findVersionsByBranch returns a list of VersionDTO
    void findVersionsByBranchSuccess() {
        BranchDTO branch = new BranchDTO("repo1", "res1", "branch1");

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("versionName", List.of("v1")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);
        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<VersionDTO> versions = repository.findVersionsByBranch(branch);

        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertEquals("v1", versions.get(0).getVersionName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}
