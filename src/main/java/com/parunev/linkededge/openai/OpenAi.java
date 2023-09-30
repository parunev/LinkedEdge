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

@Builder
public class OpenAi {
    private final String openAiApi;
    private final String openAiHost;
    protected OkHttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LELogger leLogger = new LELogger(OpenAi.class);

    public OpenAi(String openAiApi, String openAiHost, OkHttpClient client) {
        this.openAiApi = openAiApi;
        this.openAiHost = openAiHost;
        this.client = client;
    }

    public String ask(List<OpenAiMessage> messages) {
        leLogger.info("Performing 'ask' operation with default model and messages.");
        return ask(OpenAiDefaults.DEFAULT_MODEL.getValue(), messages);
    }

    // In near feature we will have the option to use gpt-4, so the user will have the ability to choose
    // which AI bot to use
    public String ask(OpenAiModel model, List<OpenAiMessage> messages) {
        leLogger.info("Performing 'ask' operation with model '{}' and messages.", model.getName());
        return ask(model.getName(), messages);
    }

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

    public OpenAiCompletionResponse askOriginal(String model, List<OpenAiMessage> messages) {
        leLogger.info("Performing 'askOriginal' operation for model '{}' with messages.", model);
        RequestBody body = RequestBody
                .create(buildRequestBody(model, messages), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(openAiHost)
                .header("Authorization", "Bearer " + openAiApi)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.body() == null) {
                    leLogger.warn("Request failed: {}, please try again", response.message());
                    throw new OpenAiException(buildError(response.code(), "Request failed"));
                } else {
                    leLogger.warn("Request failed: {}, please try again", response.body().string());
                    throw new OpenAiException(buildError(response.code(), response.body().string()));
                }
            } else {
                assert response.body() != null;
                String bodyString = response.body().string();
                return objectMapper.readValue(bodyString, OpenAiCompletionResponse.class);
            }
        } catch (IOException e) {
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
