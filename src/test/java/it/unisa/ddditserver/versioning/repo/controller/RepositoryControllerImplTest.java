package it.unisa.ddditserver.versioning.repo.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unisa.ddditserver.versioning.controller.repo.RepositoryControllerImpl;
import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.versioning.exceptions.repo.ExistingRepositoryException;
import it.unisa.ddditserver.versioning.exceptions.repo.InvalidRepositoryNameException;
import it.unisa.ddditserver.versioning.exceptions.repo.RepositoryException;
import it.unisa.ddditserver.versioning.service.repo.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RepositoryControllerImplTest {

    private MockMvc mockMvc;

    @Mock
    private RepositoryService repositoryService;

    @InjectMocks
    private RepositoryControllerImpl repositoryController;

    private String validToken;
    private SecretKey secretKey;

    @BeforeEach
    public void setup() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(repositoryController).build();

        this.secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        Field secretField = RepositoryControllerImpl.class.getDeclaredField("jwtSecretBase64");
        secretField.setAccessible(true);
        secretField.set(repositoryController, base64Key);

        repositoryController.init();

        validToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(secretKey)
                .compact();
    }

    // Create repository unit testing

    @Test
    public void createRepositorySuccess() throws Exception {
        RepositoryDTO repoDTO = new RepositoryDTO();
        repoDTO.setRepositoryName("new-repo");

        when(repositoryService.createRepository(any(RepositoryDTO.class), any(String.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Repository created successfully")));

        mockMvc.perform(post("/repositories/create")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"repositoryName\":\"new-repo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Repository created successfully"));
    }

    @Test
    public void createRepositoryInvalidName() throws Exception {
        when(repositoryService.createRepository(any(RepositoryDTO.class), any(String.class)))
                .thenThrow(new InvalidRepositoryNameException("Invalid repository name"));

        mockMvc.perform(post("/repositories/create")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"repositoryName\":\"invalid name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid repository name"));
    }

    @Test
    public void createRepositoryExisting() throws Exception {
        when(repositoryService.createRepository(any(RepositoryDTO.class), any(String.class)))
                .thenThrow(new ExistingRepositoryException("Repository already exists"));

        mockMvc.perform(post("/repositories/create")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"repositoryName\":\"existing-repo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Repository already exists"));
    }

    @Test
    public void createRepositoryUnauthorized() throws Exception {
        mockMvc.perform(post("/repositories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"repositoryName\":\"new-repo\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing, invalid, or expired Authorization token"));
    }

    // List owned repositories unit testing

    @Test
    public void listOwnedRepositoriesSuccess() throws Exception {
        List<RepositoryDTO> repos = List.of(new RepositoryDTO("repo1"), new RepositoryDTO("repo2"));
        when(repositoryService.listRepositoriesOwned(any(String.class)))
                .thenReturn(ResponseEntity.ok(Map.of("repositories", repos)));

        mockMvc.perform(get("/repositories/owned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].repositoryName").value("repo1"));
    }

    @Test
    public void listOwnedRepositoriesUnauthorized() throws Exception {
        mockMvc.perform(get("/repositories/owned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing, invalid, or expired Authorization token"));
    }

    @Test
    public void listOwnedRepositoriesException() throws Exception {
        when(repositoryService.listRepositoriesOwned(any(String.class)))
                .thenThrow(new RepositoryException("Service error"));

        mockMvc.perform(get("/repositories/owned")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Service error"));
    }

    // List contributed repositories unit testing

    @Test
    public void listContributedRepositoriesSuccess() throws Exception {
        List<RepositoryDTO> repos = List.of(new RepositoryDTO("contributed-repo"));
        when(repositoryService.listRepositoriesContributed(any(String.class)))
                .thenReturn(ResponseEntity.ok(Map.of("repositories", repos)));

        mockMvc.perform(get("/repositories/contributed")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].repositoryName").value("contributed-repo"));
    }

    @Test
    public void listContributedRepositoriesUnauthorized() throws Exception {
        mockMvc.perform(get("/repositories/contributed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing, invalid, or expired Authorization token"));
    }

    @Test
    public void listContributedRepositoriesException() throws Exception {
        when(repositoryService.listRepositoriesContributed(any(String.class)))
                .thenThrow(new RepositoryException("Service error"));

        mockMvc.perform(get("/repositories/contributed")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Service error"));
    }
}
