package com.parunev.linkededge.util.annotations.nobadwords;

import com.parunev.linkededge.util.LELogger;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class BadWordsValidator implements ConstraintValidator<NoBadWords, String> {
    private final RestTemplate restTemplate;
    private final LELogger leLogger = new LELogger(BadWordsValidator.class);

    @Value("${badwords.api}")
    private String apiKey;
    private static final String BAD_WORDS_URL = "https://api.apilayer.com/bad_words?censor_character=censor_character";

    @Override
    public void initialize(NoBadWords constraintAnnotation) {
        // This method is intentionally empty.
    }

    @Override
    public boolean isValid(String question, ConstraintValidatorContext context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(question, headers);
        int totalBadWords = 0;
        JSONObject object;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    BAD_WORDS_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            object = new JSONObject(response.getBody());

            if (object.has("bad_words_total")) {
                totalBadWords = Integer.parseInt(object.getString("bad_words_total"));
            }

        } catch (Exception e) {
            leLogger.error("An error occurred while checking for bad words Exception: {}. Message: {}.", e, e.getMessage());
        }


        return totalBadWords < 1;
    }
}
