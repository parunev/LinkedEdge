package com.parunev.linkededge.openai.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiCompletionRequest {

    /**
     * Required
     * <p>
     * ID of the model to use. Currently, only gpt-3.5-turbo is supported.
     */
    @JsonProperty(value = "model")
    private String model;

    /**
     * Required
     * <p>
     * The messages to generate chat completions for, in the
     * <a href=https://platform.openai.com/docs/guides/chat/introduction>chat format</a>.
     */
    @JsonProperty(value = "messages")
    private List<OpenAiMessage> messages;

    /**
     * Optional
     * <p>
     * Defaults to 1
     * <p>
     * What sampling temperature to use, between 0 and 2.
     * Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     */
    @JsonProperty(value = "temperature")
    private Float temperature;

    /**
     * Optional
     * <p>
     * Defaults to 0
     * <p>
     * Number between -2.0 and 2.0.
     * Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     * <p>
     * <a href=https://platform.openai.com/docs/api-reference/parameter-details>See more information about frequency and presence penalties.</a>
     */
    @JsonProperty(value = "presence_penalty")
    private Float presencePenalty;

    /**
     * Optional
     * <p>
     * Defaults to 0
     * <p>
     * Number between -2.0 and 2.0.
     * Positive values penalize new tokens based on their existing frequency in the text so far,
     * decreasing the model's likelihood to repeat the same line verbatim.
     * <p>
     * <a href=https://platform.openai.com/docs/api-reference/parameter-details>See more information about frequency and presence penalties.</a>
     */
    @JsonProperty(value = "frequency_penalty")
    private Float frequencyPenalty;

    /**
     * Optional
     * <p>
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     * <p>
     * <a href=https://platform.openai.com/docs/guides/safety-best-practices/end-user-ids>Learn more</a>.
     */
    private String user;
}
