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

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OTPConfig.class)
public class OTPCacheBean {

    private final OTPConfig otpConfig;

    @Bean
    public LoadingCache<String, Integer> loadingCache() {
        final int expirationMinutes = otpConfig.getExpirationMinutes();

        return configureCacheBuilder(expirationMinutes).build(new CacheLoader<>() {
            @NonNull
            public Integer load(@NonNull String key) {
                return 0;
            }
        });
    }



    private CacheBuilder<Object, Object> configureCacheBuilder(int expirationMinutes) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(expirationMinutes, TimeUnit.MINUTES);
    }
}
