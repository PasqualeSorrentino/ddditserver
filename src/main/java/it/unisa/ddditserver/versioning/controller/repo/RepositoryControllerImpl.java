package it.unisa.ddditserver.versioning.controller.repo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.versioning.exceptions.repo.ExistingRepositoryException;
import it.unisa.ddditserver.versioning.exceptions.repo.InvalidRepositoryNameException;
import it.unisa.ddditserver.versioning.exceptions.repo.RepositoryException;
import it.unisa.ddditserver.versioning.service.repo.RepositoryService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/repositories")
public class RepositoryControllerImpl implements RepositoryController {

    @Autowired
    private RepositoryService repositoryService;

    @Value("${JWT_SECRET}")
    private String jwtSecretBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecretBase64);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    private String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);
    }

    private ResponseEntity<Map<String, String>> unauthorizedResponse() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Missing, invalid, or expired Authorization token"));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRepository(@RequestBody RepositoryDTO repositoryDTO,
                                              HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return unauthorizedResponse();

        String username;
        try {
            username = getUsernameFromToken(token);
        } catch (Exception e) {
            return unauthorizedResponse();
        }

        UserDTO user = new UserDTO();
        user.setUsername(username);

        try {
            return repositoryService.createRepository(repositoryDTO, token);
        } catch (InvalidRepositoryNameException | ExistingRepositoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", e.getMessage()));
        }
    }

    @GetMapping("/owned")
    public ResponseEntity<?> listOwnedRepositories(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return unauthorizedResponse();

        try {
            return repositoryService.listRepositoriesOwned(token);
        } catch (RepositoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", e.getMessage()));
        }
    }

    @GetMapping("/contributed")
    public ResponseEntity<?> listContributedRepositories(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) return unauthorizedResponse();

        try {
            return repositoryService.listRepositoriesContributed(token);
        } catch (RepositoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", e.getMessage()));
        }
    }
}

