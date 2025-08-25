package it.unisa.ddditserver.db.unit.gremlin.auth;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.auth.GremlinAuthRepositoryImpl;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.mockito.*;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
class GremlinAuthRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinAuthRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinAuthRepositoryImpl(config);

        Field clientField = GremlinAuthRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: saveUser submits a query without throwing an exception
    void saveUserSuccess() {
        UserDTO user = new UserDTO("user1", "pass123");

        assertDoesNotThrow(() -> repository.saveUser(user));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findByUser returns a valid UserDTO when user exists
    void findByUserReturnsUserDTO() {
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("password", List.of("pass123")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        UserDTO result = repository.findByUser(user);

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals("pass123", result.getPassword());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByUser returns true when user exists
    void existsByUserReturnsTrue() {
        UserDTO user = new UserDTO("user1", null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of("password", List.of("pass123")));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByUser(user);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByUser returns false when user does not exist
    void existsByUserReturnsFalse() {
        UserDTO user = new UserDTO("user1", null);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByUser(user);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}
