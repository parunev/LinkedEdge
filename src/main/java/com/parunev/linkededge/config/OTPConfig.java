package com.parunev.linkededge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: Configuration class for OTP (One-Time Password) properties.
 * <p>
 * This class provides configuration for OTP properties, specifically the expiration time
 * for generated one-time passwords (OTP). OTPs are commonly used for secure authentication and verification.
 *
 * @author Martin Parunev
 * @date  October 12, 2023
 */
@Data
@ConfigurationProperties(prefix = "linked.otp")
public class OTPConfig {

    /**
     * Expiration time in minutes for the generated one-time passwords (OTP). Coming from the application properties
     */
    private final Integer expirationMinutes;
}
