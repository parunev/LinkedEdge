package com.parunev.linkededge.security.mfa;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.parunev.linkededge.config.OTPConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
/**
 * @Description: The `OTPCacheBean` class is a configuration bean that defines a cache for storing OTP (One-Time Password) values.
 * It uses Google Guava's LoadingCache to store and manage OTP values with a defined expiration time.
 */

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OTPConfig.class)
public class OTPCacheBean {

    private final OTPConfig otpConfig; // Configuration properties for OTP cache settings

    /**
     * Create and configure a LoadingCache for storing OTP values.
     *
     * @return A LoadingCache instance for OTP storage.
     */
    @Bean
    public LoadingCache<String, Integer> loadingCache() {
        final int expirationMinutes = otpConfig.getExpirationMinutes();

        return configureCacheBuilder(expirationMinutes).build(new CacheLoader<>() {
            @NonNull
            public Integer load(@NonNull String key) {
                return 0; // Initial value for the cache when loading a new key
            }
        });
    }

    /**
     * Configure the cache builder with an expiration time for OTP values.
     *
     * @param expirationMinutes The duration in minutes before entries expire in the cache.
     * @return A CacheBuilder with the defined expiration settings.
     */
    private CacheBuilder<Object, Object> configureCacheBuilder(int expirationMinutes) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(expirationMinutes, TimeUnit.MINUTES);
    }
}
