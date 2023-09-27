package com.parunev.linkededge.security.jwt;

import com.parunev.linkededge.model.User;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
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

    public String extractEmail(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public String generateToken(User userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(User userDetails){
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims,
                                User userDetails){
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String buildToken(Map<String, Object> extraClaims,
                             User userDetails,
                             long expiration) {

        JwtClaimsSet.Builder jwtClaimsSetBuilder = buildJwtClaims(userDetails, expiration);

        for (Map.Entry<String, Object> entry : createAndReturnClaims(userDetails, extraClaims).entrySet()) {
            jwtClaimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetBuilder.build())).getTokenValue();
    }

    private JwtClaimsSet.Builder buildJwtClaims(User user, long expiration) {
        return JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuer(ISSUER)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofMillis(expiration)));
    }

    private Map<String, Object> createAndReturnClaims(User user, Map<String, Object> extraClaims) {
        Map<String, Object> claim = new HashMap<>();
        claim.put(SCOPE, user.getAuthority().getAuthority());
        claim.putAll(extraClaims);

        return claim;
    }

    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = Jwts.claims(extractClaim(token));
        claims.setExpiration(Date.from(Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt())));
        return claimsResolver.apply(claims);
    }

    public Map<String, Object> extractClaim(String token) {
        return jwtDecoder.decode(token).getClaims();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        User user = findUserByUsername(userDetails.getUsername());
        return (extractEmail(token).equals(user.getEmail())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    private User findUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new UserNotFoundException(
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
