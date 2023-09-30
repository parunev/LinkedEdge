package com.parunev.linkededge.config;

import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.enums.OpenAiDefaults;
import com.parunev.linkededge.security.SpringSecurityAuditorAware;
import com.parunev.linkededge.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@Getter
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableConfigurationProperties(value = {RsaConfiguration.class})
public class ApplicationConfiguration {
    private final UserService userService;

    @Value("${openai.api}")
    private String openAiApi;
    private final String openAiHost = OpenAiDefaults.DEFAULT_URL.getValue();

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(14);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OkHttpClient client(){
        // really depends on what type of questions are asked
        // 300 seconds = 5 minutes
        return new OkHttpClient.Builder()
              .connectTimeout(300, TimeUnit.SECONDS)
              .readTimeout(300, TimeUnit.SECONDS)
              .writeTimeout(300, TimeUnit.SECONDS)
              .build();
    }

    @Bean
    public OpenAi openAi(){
        return new OpenAi(openAiApi, openAiHost, client());
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}
