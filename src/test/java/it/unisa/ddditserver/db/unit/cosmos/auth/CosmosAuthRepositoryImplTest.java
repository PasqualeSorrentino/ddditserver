package it.unisa.ddditserver.db.unit.cosmos.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.auth0.jwt.JWT;
import it.unisa.ddditserver.db.cosmos.auth.CosmosAuthRepositoryImpl;
import it.unisa.ddditserver.subsystems.auth.dto.BlacklistedTokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
public class CosmosAuthRepositoryImplTest {
    @Mock
    private CosmosContainer blacklistContainer;

    @InjectMocks
    private CosmosAuthRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field blackListField = CosmosAuthRepositoryImpl.class.getDeclaredField("blacklistContainer");
        blackListField.setAccessible(true);
        blackListField.set(repository, blacklistContainer);
    }

    @Test
    // Happy path: Valid token blacklisted successfully
    void blacklistTokenSuccess() {
        String token = JWT.create()
                .withIssuer("test")
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 60000)) // expires in 60s
                .sign(Algorithm.HMAC256("secret"));

        repository.blacklistToken(token);

        verify(blacklistContainer, times(1)).upsertItem(any());
    }

    @Test
    // Happy path: Returns true when the token exists in Cosmos DB document
    void isTokenBlacklistedReturnsTrue() {
        String token = "test-token";

        BlacklistedTokenDTO mockDTO = new BlacklistedTokenDTO(token, token, 60);
        CosmosItemResponse<BlacklistedTokenDTO> mockResponse = mock(CosmosItemResponse.class);
        when(mockResponse.getItem()).thenReturn(mockDTO);

        when(blacklistContainer.readItem(eq(token), any(PartitionKey.class), eq(BlacklistedTokenDTO.class)))
                .thenReturn(mockResponse);

        boolean result = repository.isTokenBlacklisted(token);

        assertTrue(result);
        verify(blacklistContainer, times(1)).readItem(eq(token), any(PartitionKey.class), eq(BlacklistedTokenDTO.class));
    }

    @Test
    // Happy path: Returns false when the token doesn't exist in Cosmos DB document
    void isTokenBlacklistedReturnsFalse() {
        String token = "test-token";

        CosmosException notFoundException = mock(CosmosException.class);
        when(notFoundException.getStatusCode()).thenReturn(404);

        when(blacklistContainer.readItem(eq(token), any(PartitionKey.class), eq(BlacklistedTokenDTO.class)))
                .thenThrow(notFoundException);

        boolean result = repository.isTokenBlacklisted(token);

        assertFalse(result);
        verify(blacklistContainer, times(1)).readItem(eq(token), any(PartitionKey.class), eq(BlacklistedTokenDTO.class));
    }
}
