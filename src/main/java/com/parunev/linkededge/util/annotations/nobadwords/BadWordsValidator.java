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

/**
 * Custom constraint validator for the {@link NoBadWords} annotation.
 * @see <a href="https://apilayer.com/marketplace/bad_words-api#:~:text=Detects%20bad%20words%2C%20swear%20words,check%20in%20a%20given%20text.&text=An%20advanced%20profanity%20filter%20based,words%20in%20a%20given%20text.">BadWordsAPI</a>
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
@RequiredArgsConstructor
public class BadWordsValidator implements ConstraintValidator<NoBadWords, String> {
    private final RestTemplate restTemplate;
    private final LELogger leLogger = new LELogger(BadWordsValidator.class);

    @Value("${badwords.api}")
    private String apiKey;
    private static final String BAD_WORDS_URL = "https://api.apilayer.com/bad_words?censor_character=censor_character";

    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation The annotation to be initialized (in this case, {@link NoBadWords}).
     */
    @Override
    public void initialize(NoBadWords constraintAnnotation) {
        // This method is intentionally empty.
    }

    /**
     * Validates whether the provided text (question) contains bad words.
     * <p>
     * This method sends the provided text to an external bad words detection API for analysis. It checks if the text
     * contains any prohibited or offensive language. The response from the API is examined to determine the presence of
     * bad words, and the result is returned as a boolean.
     *
     * @param question The text to be validated for bad words.
     * @param context The validation context.
     * @return {@code true} if the text does not contain bad words; otherwise, {@code false}.
     */
    @Override
    public boolean isValid(String question, ConstraintValidatorContext context) {
        // Set up HTTP headers and entity for API request
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", apiKey);
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(question, headers);
        // Initialize variables to track bad words and API response
        int totalBadWords = 0;
        JSONObject object;

        try {
            // Send a POST request to the bad words detection API
            ResponseEntity<String> response = restTemplate.exchange(
                    BAD_WORDS_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parse the API response into a JSON object
            object = new JSONObject(response.getBody());

            // Check if the API response contains a count of bad words
            if (object.has("bad_words_total")) {
                totalBadWords = Integer.parseInt(object.getString("bad_words_total"));
            }

        } catch (Exception e) {
            // Handle exceptions and log errors
            leLogger.error("An error occurred while checking for bad words Exception: {}. Message: {}.", e, e.getMessage());
        }

        // Return true if no bad words were found, otherwise return false
        return totalBadWords < 1;
    }
}
