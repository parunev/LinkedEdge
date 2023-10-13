package com.parunev.linkededge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @Description: Configuration class for RSA (Rivest–Shamir–Adleman) key pair.
 * <p>
 * This class provides configuration properties for RSA (Rivest–Shamir–Adleman) key pair,
 * including a public key and a private key. RSA keys are commonly used for secure encryption
 * and decryption in various cryptographic operations.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */

@ConfigurationProperties(prefix = "rsa")
public record RsaConfiguration(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
