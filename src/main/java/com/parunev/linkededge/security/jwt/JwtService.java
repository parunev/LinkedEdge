package com.parunev.linkededge.security.jwt;

import com.parunev.linkededge.model.User;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.payload.ApiError;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * @Description: The `JwtService` class is responsible for handling JSON Web Token (JWT) operations,
 * including token generation, validation, and extraction.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    private static final String SCOPE = "scope";

    private static final String ISSUER = "LinkedEdge_API";

    /**
     * Extract the email from a given JWT token.
     *
     * @param token The JWT token from which to extract the email
     * @return The email address associated with the token
     */
    public String extractEmail(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    /**
     * Generate a JWT token for a given user.
     *
     * @param userDetails The user for whom the token is generated
     * @return The generated JWT token
     */
    public String generateToken(User userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a refresh JWT token for a given user.
     *
     * @param userDetails The user for whom the token is generated
     * @return The generated refresh JWT token
     */
    public String generateRefreshToken(User userDetails){
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Generate a JWT token with extra claims for a given user.
     *
     * @param extraClaims Additional claims to include in the token
     * @param userDetails  The user for whom the token is generated
     * @return The generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims,
                                User userDetails){
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Build a JWT token with specific claims and user details.
     *
     * @param extraClaims Additional claims to include in the token
     * @param userDetails  The user for whom the token is generated
     * @param expiration   The expiration time for the token
     * @return The generated JWT token
     */
    public String buildToken(Map<String, Object> extraClaims,
                             User userDetails,
                             long expiration) {

        JwtClaimsSet.Builder jwtClaimsSetBuilder = buildJwtClaims(userDetails, expiration);

        for (Map.Entry<String, Object> entry : createAndReturnClaims(userDetails, extraClaims).entrySet()) {
            jwtClaimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetBuilder.build())).getTokenValue();
    }

    /**
     * Build JWT claims with user details and expiration.
     *
     * @param user       The user for whom the claims are generated
     * @param expiration The expiration time for the claims
     * @return The JWT claims
     */
    private JwtClaimsSet.Builder buildJwtClaims(User user, long expiration) {
        return JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuer(ISSUER)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofMillis(expiration)));
    }

    /**
     * Create and return JWT claims for user details and extra claims.
     *
     * @param user        The user for whom the claims are generated
     * @param extraClaims Additional claims to include
     * @return The JWT claims
     */
    private Map<String, Object> createAndReturnClaims(User user, Map<String, Object> extraClaims) {
        Map<String, Object> claim = new HashMap<>();
        claim.put(SCOPE, user.getAuthority().getAuthority());
        claim.putAll(extraClaims);

        return claim;
    }

    /**
     * Extract the expiration date from a JWT token.
     *
     * @param token The JWT token from which to extract the expiration date
     * @return The expiration date as an Instant
     */
    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }

    /**
     * Extract a specific claim from a JWT token.
     *
     * @param token          The JWT token from which to extract the claim
     * @param claimsResolver A function to resolve the claim
     * @param <T>            The type of the claim
     * @return The extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = Jwts.claims(extractClaim(token));
        claims.setExpiration(Date.from(Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt())));
        return claimsResolver.apply(claims);
    }

    /**
     * Extract claims from a JWT token.
     *
     * @param token The JWT token from which to extract claims
     * @return The JWT claims as a map of key-value pairs
     */
    public Map<String, Object> extractClaim(String token) {
        return jwtDecoder.decode(token).getClaims();
    }

    /**
     * Check if a JWT token is valid.
     *
     * @param token        The JWT token to validate
     * @param userDetails  The user details for comparison
     * @return `true` if the token is valid, `false` otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        User user = findUserByUsername(userDetails.getUsername());
        return (extractEmail(token).equals(user.getEmail())) && !isTokenExpired(token);
    }

    /**
     * Check if a JWT token is expired.
     *
     * @param token The JWT token to check for expiration
     * @return `true` if the token is expired, `false` otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    /**
     * Find a user by their username.
     *
     * @param username The username of the user to find
     * @return The user with the provided username
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new ResourceNotFoundException(
                            ApiError.builder()
                                    .path(getCurrentRequest())
                                    .error("User with the provided username not found. Please ensure you have entered correct username")
                                    .timestamp(LocalDateTime.now())
                                    .status(HttpStatus.NOT_FOUND)
                                    .build());
                }
        );
    }
}
