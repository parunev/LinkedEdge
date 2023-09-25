package com.parunev.linkededge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "linked.otp")
public class OTPConfig {
    private final Integer expirationMinutes;
}
