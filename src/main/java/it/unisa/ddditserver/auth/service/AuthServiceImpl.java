package it.unisa.ddditserver.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.auth.exceptions.AuthException;
import it.unisa.ddditserver.auth.exceptions.ExistingUserException;
import it.unisa.ddditserver.auth.exceptions.InvalidCredentialsException;
import it.unisa.ddditserver.db.gremlin.auth.GremlinAuthService;
import it.unisa.ddditserver.validators.ValidationResult;
import it.unisa.ddditserver.validators.user.UserValidationDTO;
import it.unisa.ddditserver.validators.user.UserValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private GremlinAuthService gremlinService;
    @Autowired
    private UserValidator userValidator;

    @Value("${JWT_SECRET}")
    private String jwtSecretBase64;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecretBase64);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    private String generateToken(String username) {
        long expirationMillis = 1000 * 60 * 1440; // 24 hours
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public ResponseEntity<Map<String, String>> signup(UserDTO user) {
        UserValidationDTO userValidationDTO = new UserValidationDTO(user.getUsername(), user.getPassword());

        String username = user.getUsername();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Check if user's credentials are well-formed
        ValidationResult validationResult = userValidator.validateCredentials(userValidationDTO);
        if (!validationResult.isValid()) {
            throw new InvalidCredentialsException(validationResult.getMessage());
        }

        // Check if the username already exists in graph database
        validationResult = userValidator.validateExistence(userValidationDTO);
        if(!validationResult.isValid()) {
            throw new ExistingUserException(validationResult.getMessage());
        }

        // Save user's information on graph database and generate a JWT token for authentication
        try {
            gremlinService.saveUser(user);
            String token = generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            throw new AuthException("Signup failed during saving's operations on database");
        }
    }
}
