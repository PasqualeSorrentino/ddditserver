package it.unisa.ddditserver.versioning.service.repo;

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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class RepositoryServiceImpl implements  RepositoryService {
    @Autowired
    private GremlinRepositoryService gremlinService;

    @Autowired
    private RepositoryValidator repositoryValidator;

    @Value("${JWT_SECRET}")
    public String jwtSecretBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecretBase64);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    private String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> createRepository(RepositoryDTO repository, String token) {
        String username = getUsernameFromToken(token);

        RepositoryValidationDTO repositoryValidationDTO = new RepositoryValidationDTO(repository.getRepositoryName());

        ValidationResult validationResult = repositoryValidator.validateName(repositoryValidationDTO);
        if (!validationResult.isValid()) {
            throw new InvalidRepositoryNameException(validationResult.getMessage());
        }

        validationResult = repositoryValidator.validateExistence(repositoryValidationDTO, true);
        if (validationResult.isValid()) {
            throw new ExistingRepositoryException(validationResult.getMessage());
        }

        try {
            gremlinService.saveRepository(repository, new UserDTO(username, null));
            return ResponseEntity.ok(Map.of("message", "Repository created successfully"));
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> listRepositoriesOwned(String token) {
        String username = getUsernameFromToken(token);

        try {
            List<RepositoryDTO> ownedRepos = gremlinService.findOwnedRepositoriesByUser(new UserDTO(username, null));
            return ResponseEntity.ok(Map.of("repositories", ownedRepos));
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> listRepositoriesContributed(String token) {
        String username = getUsernameFromToken(token);

        try {
            List<RepositoryDTO> contributedRepos = gremlinService.findContributedRepositoriesByUser(new UserDTO(username, null));
            return ResponseEntity.ok(Map.of("repositories", contributedRepos));
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }
    }
}
