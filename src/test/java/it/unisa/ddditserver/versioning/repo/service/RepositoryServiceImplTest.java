package it.unisa.ddditserver.versioning.repo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.db.gremlin.versioning.repo.GremlinRepositoryService;
import it.unisa.ddditserver.validators.ValidationResult;
import it.unisa.ddditserver.validators.repo.RepositoryValidationDTO;
import it.unisa.ddditserver.validators.repo.RepositoryValidator;
import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.versioning.exceptions.repo.ExistingRepositoryException;
import it.unisa.ddditserver.versioning.exceptions.repo.InvalidRepositoryNameException;
import it.unisa.ddditserver.versioning.exceptions.repo.RepositoryException;
import it.unisa.ddditserver.versioning.service.repo.RepositoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryServiceImplTest {

    @Mock
    private GremlinRepositoryService gremlinService;

    @Mock
    private RepositoryValidator repositoryValidator;

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    private SecretKey secretKey;
    private String validToken;

    @BeforeEach
    public void setUp() throws Exception {

        this.secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        Field secretField = RepositoryServiceImpl.class.getDeclaredField("jwtSecretBase64");
        secretField.setAccessible(true);
        secretField.set(repositoryService, base64Key);

        repositoryService.init();

        validToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(secretKey)
                .compact();
    }

    // Create repository unit testing

    @Test
    void createRepositorySuccess() {
        RepositoryDTO repoDTO = new RepositoryDTO("new-repo");

        ValidationResult validResult = mock(ValidationResult.class);
        when(validResult.isValid()).thenReturn(true);

        ValidationResult invalidResult = mock(ValidationResult.class);
        when(invalidResult.isValid()).thenReturn(false);

        when(repositoryValidator.validateName(any(RepositoryValidationDTO.class))).thenReturn(validResult);
        when(repositoryValidator.validateExistence(any(RepositoryValidationDTO.class), eq(true))).thenReturn(invalidResult);

        doNothing().when(gremlinService).saveRepository(any(), any());

        ResponseEntity<Map<String, String>> response = repositoryService.createRepository(repoDTO, validToken);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Repository created successfully", response.getBody().get("message"));
        verify(gremlinService, times(1)).saveRepository(any(), any());
    }

    @Test
    void createRepositoryThrowsInvalidRepositoryNameException() {
        RepositoryDTO repoDTO = new RepositoryDTO("invalid name");

        ValidationResult invalidResult = mock(ValidationResult.class);
        when(invalidResult.isValid()).thenReturn(false);
        when(invalidResult.getMessage()).thenReturn("Invalid repository name");

        when(repositoryValidator.validateName(any(RepositoryValidationDTO.class))).thenReturn(invalidResult);

        InvalidRepositoryNameException e = assertThrows(InvalidRepositoryNameException.class, () ->
                repositoryService.createRepository(repoDTO, validToken));
        assertEquals("Invalid repository name", e.getMessage());
    }

    @Test
    void createRepositoryThrowsExistingRepositoryException() {
        RepositoryDTO repoDTO = new RepositoryDTO("existing-repo");

        ValidationResult validResult = mock(ValidationResult.class);
        when(validResult.isValid()).thenReturn(true);

        ValidationResult invalidResult = mock(ValidationResult.class);
        when(invalidResult.isValid()).thenReturn(true);
        when(invalidResult.getMessage()).thenReturn("Repository already exists");

        when(repositoryValidator.validateName(any(RepositoryValidationDTO.class))).thenReturn(validResult);
        when(repositoryValidator.validateExistence(any(RepositoryValidationDTO.class), eq(true))).thenReturn(invalidResult);

        ExistingRepositoryException e = assertThrows(ExistingRepositoryException.class, () ->
                repositoryService.createRepository(repoDTO, validToken));
        assertEquals("Repository already exists", e.getMessage());
    }

    @Test
    void createRepositoryThrowsRepositoryException() {
        RepositoryDTO repoDTO = new RepositoryDTO("new-repo");

        ValidationResult validResult = mock(ValidationResult.class);
        when(validResult.isValid()).thenReturn(true);

        ValidationResult invalidResult = mock(ValidationResult.class);
        when(invalidResult.isValid()).thenReturn(false);

        when(repositoryValidator.validateName(any(RepositoryValidationDTO.class))).thenReturn(validResult);
        when(repositoryValidator.validateExistence(any(RepositoryValidationDTO.class), eq(true))).thenReturn(invalidResult);

        doThrow(new RuntimeException("DB error")).when(gremlinService).saveRepository(any(), any());

        RepositoryException e = assertThrows(RepositoryException.class, () ->
                repositoryService.createRepository(repoDTO, validToken));
        assertEquals("DB error", e.getMessage());
    }

    // List owned repositories unit testing

    @Test
    void listRepositoriesOwnedSuccess() {
        List<RepositoryDTO> repos = List.of(new RepositoryDTO("owned-repo"));
        when(gremlinService.findOwnedRepositoriesByUser(any(UserDTO.class))).thenReturn(repos);

        ResponseEntity<Map<String, Object>> response = repositoryService.listRepositoriesOwned(validToken);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("repositories"));
        List<RepositoryDTO> returnedRepos = (List<RepositoryDTO>) response.getBody().get("repositories");
        assertEquals(1, returnedRepos.size());
        assertEquals("owned-repo", returnedRepos.get(0).getRepositoryName());
    }

    @Test
    void listRepositoriesOwnedThrowsRepositoryException() {
        when(gremlinService.findOwnedRepositoriesByUser(any(UserDTO.class)))
                .thenThrow(new RuntimeException("DB error"));

        RepositoryException e = assertThrows(RepositoryException.class, () ->
                repositoryService.listRepositoriesOwned(validToken));
        assertEquals("DB error", e.getMessage());
    }

    // List contributed repositories unit testing

    @Test
    void listRepositoriesContributedSuccess() {
        List<RepositoryDTO> repos = List.of(new RepositoryDTO("contributed-repo"));
        when(gremlinService.findContributedRepositoriesByUser(any(UserDTO.class))).thenReturn(repos);

        ResponseEntity<Map<String, Object>> response = repositoryService.listRepositoriesContributed(validToken);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("repositories"));
        List<RepositoryDTO> returnedRepos = (List<RepositoryDTO>) response.getBody().get("repositories");
        assertEquals(1, returnedRepos.size());
        assertEquals("contributed-repo", returnedRepos.get(0).getRepositoryName());
    }

    @Test
    void listRepositoriesContributedThrowsRepositoryException() {
        when(gremlinService.findContributedRepositoriesByUser(any(UserDTO.class)))
                .thenThrow(new RuntimeException("DB error"));

        RepositoryException e = assertThrows(RepositoryException.class, () ->
                repositoryService.listRepositoriesContributed(validToken));
        assertEquals("DB error", e.getMessage());
    }
}