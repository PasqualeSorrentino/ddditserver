package it.unisa.ddditserver.db.unit.gremlin.invitation;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.invitation.GremlinInvitationRepositoryImpl;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.invitation.dto.InvitationDTO;
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
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
class GremlinInvitationRepositoryImplTest {
    @Mock
    private GremlinConfig config;

    @Mock
    private Client client;

    @InjectMocks
    private GremlinInvitationRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        repository = new GremlinInvitationRepositoryImpl(config);

        Field clientField = GremlinInvitationRepositoryImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(repository, client);
    }

    @Test
    // Happy path: saveInvitation submits a query without throwing an exception
    void saveInvitationSuccess() {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        assertDoesNotThrow(() -> repository.saveInvitation(fromUser, toUser, repo));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByUserAndRepository returns true when invitation exists
    void existsByUserAndRepositoryReturnsTrue() throws Exception {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        Result mockResult = mock(Result.class);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByUserAndRepository(fromUser, toUser, repo);

        assertTrue(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: existsByUserAndRepository returns false when invitation does not exist
    void existsByUserAndRepositoryReturnsFalse() throws Exception {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean exists = repository.existsByUserAndRepository(fromUser, toUser, repo);

        assertFalse(exists);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: acceptInvitation submits a query without throwing an exception
    void acceptInvitationSuccess() {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        assertDoesNotThrow(() -> repository.acceptInvitation(fromUser, toUser, repo));
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isAcceptedInvitation returns true when invitation is accepted
    void isAcceptedInvitationReturnsTrue() throws Exception {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        Result mockResult = mock(Result.class);

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean accepted = repository.isAcceptedInvitation(fromUser, toUser, repo);

        assertTrue(accepted);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: isAcceptedInvitation returns false when invitation is not accepted
    void isAcceptedInvitationReturnsFalse() throws Exception {
        UserDTO fromUser = new UserDTO("user1", null);
        UserDTO toUser = new UserDTO("user2", null);
        RepositoryDTO repo = new RepositoryDTO("repo1");

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of());
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        boolean accepted = repository.isAcceptedInvitation(fromUser, toUser, repo);

        assertFalse(accepted);
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }

    @Test
    // Happy path: findInvitationsByUser returns a list of pending invitations
    void findInvitationsByUserReturnsUserDTO() throws Exception {
        UserDTO toUser = new UserDTO("user2", null);

        Result mockResult = mock(Result.class);
        when(mockResult.getObject()).thenReturn(Map.of(
                "fromUsername", "user1",
                "repositoryName", "repo1"
        ));

        ResultSet mockResultSet = mock(ResultSet.class);
        CompletableFuture<List<Result>> future = CompletableFuture.completedFuture(List.of(mockResult));
        when(mockResultSet.all()).thenReturn(future);

        when(client.submit(anyString(), any(Map.class))).thenReturn(mockResultSet);

        List<InvitationDTO> invitations = repository.findInvitationsByUser(toUser);

        assertNotNull(invitations);
        assertEquals(1, invitations.size());
        assertEquals("user1", invitations.get(0).getToUsername());
        assertEquals("repo1", invitations.get(0).getRepositoryName());
        verify(client, times(1)).submit(anyString(), any(Map.class));
    }
}

