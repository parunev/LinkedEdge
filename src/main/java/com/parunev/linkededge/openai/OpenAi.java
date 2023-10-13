package com.parunev.linkededge.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parunev.linkededge.openai.exception.OpenAiException;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.openai.model.enums.OpenAiDefaults;
import com.parunev.linkededge.openai.model.enums.OpenAiError;
import com.parunev.linkededge.openai.model.enums.OpenAiModel;
import com.parunev.linkededge.openai.model.payload.OpenAiCompletionRequest;
import com.parunev.linkededge.openai.model.payload.OpenAiCompletionResponse;
import com.parunev.linkededge.security.exceptions.InvalidWritingException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import lombok.Builder;
import okhttp3.*;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * @Description: Class for interacting with OpenAI's language models.
 * <p>
 * This class facilitates interactions with OpenAI's language models for generating chat completions.
 * It provides methods for asking questions and retrieving responses. It allows customization of the model used
 * and various parameters for fine-tuning the chat generation process.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Builder
public class OpenAi {
    private final String openAiApi;
    private final String openAiHost;
    protected OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LELogger leLogger = new LELogger(OpenAi.class);

    /**
     * Constructor for the OpenAi class.
     *
     * @param openAiApi The OpenAI API key.
     * @param openAiHost The OpenAI API host.
     * @param client The OkHttpClient for making HTTP requests.
     */
    public OpenAi(String openAiApi, String openAiHost, OkHttpClient client) {
        this.openAiApi = openAiApi;
        this.openAiHost = openAiHost;
        this.client = client;
    }

    /**
     * Ask a question using the default model and provided messages.
     *
     * @param messages A list of messages in the chat conversation.
     * @return The generated response as a string.
     */
    public String ask(List<OpenAiMessage> messages) {
        leLogger.info("Performing 'ask' operation with default model and messages.");
        return ask(OpenAiDefaults.DEFAULT_MODEL.getValue(), messages);
    }

    /**
     * Ask a question using a specific model and provided messages.
     *
     * @param model The specific OpenAI model to use.
     * @param messages A list of messages in the chat conversation.
     * @return The generated response as a string.
     */
    public String ask(OpenAiModel model, List<OpenAiMessage> messages) {
        leLogger.info("Performing 'ask' operation with model '{}' and messages.", model.getName());
        return ask(model.getName(), messages);
    }

    /**
     * Ask a question using a specific model and provided messages.
     *
     * @param model The specific OpenAI model to use.
     * @param message A list of messages in the chat conversation.
     * @return The generated response as a string.
     */
    public String ask(String model, List<OpenAiMessage> message) {
        leLogger.info("Performing 'ask' operation for model '{}' with messages.", model);
        OpenAiCompletionResponse response = askOriginal(model, message);
        List<OpenAiCompletionResponse.Choice> choices = response.getChoices();
        StringBuilder result = new StringBuilder();

        for (OpenAiCompletionResponse.Choice choice : choices) {
            result.append(choice.getMessage().getContent());
        }

        return result.toString();
    }

    /**
     * Perform the original 'ask' operation with a specific model and provided messages.
     * <p>
     * This method is responsible for making an HTTP request to OpenAI's chat completion API using a specific model and a list of messages.
     * It allows you to customize and fine-tune the chat generation process.
     *
     * @param model The specific OpenAI model to use for generating chat completions.
     * @param messages A list of messages in the chat conversation, including roles (e.g., "user" or "assistant") and content.
     * @return An OpenAiCompletionResponse object representing the response from OpenAI.
     * @throws OpenAiException If the request to OpenAI's API fails, an exception is thrown with error details.
     */
    public OpenAiCompletionResponse askOriginal(String model, List<OpenAiMessage> messages) {
        leLogger.info("Performing 'askOriginal' operation for model '{}' with messages.", model);
        // Create the request body by serializing the provided model and messages into JSON.
        RequestBody body = RequestBody
                .create(buildRequestBody(model, messages), MediaType.get("application/json; charset=utf-8"));

        // Build the HTTP request with the necessary headers and request body.
        Request request = new Request.Builder()
                .url(openAiHost)
                .header("Authorization", "Bearer " + openAiApi)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // Check if the response from OpenAI's API is successful.
            if (!response.isSuccessful()) {
                if (response.body() == null) {
                    // If the response is not successful and there's no response body, log a warning and throw an OpenAiException.
                    leLogger.warn("Request failed: {}, please try again", response.message());
                    throw new OpenAiException(buildError(response.code(), "Request failed"));
                } else {
                    // If the response is not successful and there's a response body, log a warning, and throw an OpenAiException with error details.
                    leLogger.warn("Request failed: {}, please try again", response.body().string());
                    throw new OpenAiException(buildError(response.code(), response.body().string()));
                }
            } else {
                // If the response is successful, read the response body and parse it into an OpenAiCompletionResponse object.
                assert response.body() != null;
                String bodyString = response.body().string();
                return objectMapper.readValue(bodyString, OpenAiCompletionResponse.class);
            }
        } catch (IOException e) {
            // If an IOException occurs during the request, log an error and throw an OpenAiException with error details.
            leLogger.error("Request failed: {} {}",e , e.getMessage());
            throw new OpenAiException(buildError(OpenAiError.SERVER_HAD_AN_ERROR.getCode(), e.getMessage()));
        }
    }

    private String buildRequestBody(String model, List<OpenAiMessage> messages) {
        try {
            OpenAiCompletionRequest requestBody = OpenAiCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(0.2f)
                    .presencePenalty(0.2f)
                    .build();
            return objectMapper.writeValueAsString(requestBody);

        } catch (JsonProcessingException e) {
            throw new InvalidWritingException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Failed to serialize the request body to JSON: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    private ApiError buildError(Integer statusCode, String message){
        OpenAiError openAiError = getOpenAiErrorByStatusCode(statusCode);
        String error = openAiError != null ? openAiError.getMsg() : message;
        leLogger.warn("Building error response: {}", error);

        return ApiError.builder()
                .path(getCurrentRequest())
                .error(error)
                .status(HttpStatus.valueOf(statusCode))
                .timestamp(LocalDateTime.now())
                .build();
    }

    private OpenAiError getOpenAiErrorByStatusCode(Integer statusCode) {
        for (OpenAiError error : OpenAiError.values()) {
            if (error.getCode().equals(statusCode)) {
                return error;
            }
        }
        return null;
    }
}
