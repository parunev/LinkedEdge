package com.parunev.linkededge.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * @Description: Configuration class for handling JWT (JSON Web Token) encoding and decoding.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {
    private final RsaConfiguration rsaConfiguration;

    /**
     * Bean definition for JWT decoder, responsible for decoding JWTs.
     * <p>
     * @Explanation: This bean is used to verify and decode JWTs, which are commonly used for securing web applications and APIs.
     * It uses a public key to verify the JWT's signature, ensuring the token's integrity.
     * The SignatureAlgorithm.RS256 indicates that the JWTs are signed using the RSA algorithm with a 256-bit key.
     * This bean is essential for validating JWTs to ensure that they haven't been tampered with and originate from a trusted source.
     *
     * @return JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaConfiguration.publicKey()).signatureAlgorithm(SignatureAlgorithm.RS256).build();
    }

    /**
     * Bean definition for JWT encoder, responsible for encoding JWTs.
     *
     * @Explanation: This bean is used to create and encode JWTs.
     * It takes a private key (RSA key pair) and uses it to sign JWTs, ensuring their authenticity and integrity.
     * JWTs typically carry information about the user's identity and authorization, and they are digitally signed to prevent tampering.
     * The JwtEncoder uses the provided RSA key pair to create signed JWTs,
     * which can be issued to clients as tokens for authentication and authorization purposes.
     *
     * @return JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaConfiguration.publicKey())
                .privateKey(rsaConfiguration.privateKey())
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

}
