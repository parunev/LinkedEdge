package com.parunev.linkededge.openai.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: Payload class for making requests to generate chat completions using OpenAI.
 * <p>
 * This class represents a request payload for generating chat completions with OpenAI's language model.
 * It includes various parameters such as the model ID, chat messages, sampling temperature, presence penalty,
 * frequency penalty, and user identifier to customize and fine-tune the chat generation process.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAiCompletionRequest {

    /**
     * Required: ID of the model to use. Currently, only "gpt-3.5-turbo" is supported.
     */
    @JsonProperty(value = "model")
    private String model;

    /**
     * Required: The messages to generate chat completions for, in the chat format.
     * @see <a href="https://platform.openai.com/docs/guides/chat/introduction">OpenAI Chat Format</a>
     */
    @JsonProperty(value = "messages")
    private List<OpenAiMessage> messages;

    /**
     * Optional: Defaults to 1. Specifies the sampling temperature to use, between 0 and 2.
     * Higher values like 0.8 make the output more random, while lower values like 0.2 make it more focused and deterministic.
     */
    @JsonProperty(value = "temperature")
    private Float temperature;

    /**
     * Optional: Defaults to 0. A number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     * @see <a href="https://platform.openai.com/docs/api-reference/parameter-details">Frequency and Presence Penalties</a>
     */
    @JsonProperty(value = "presence_penalty")
    private Float presencePenalty;

    /**
     * Optional: Defaults to 0. A number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far,
     * decreasing the model's likelihood to repeat the same line verbatim.
     * @see <a href="https://platform.openai.com/docs/api-reference/parameter-details">Frequency and Presence Penalties</a>
     */
    @JsonProperty(value = "frequency_penalty")
    private Float frequencyPenalty;

    /**
     * Optional: A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     * @see <a href="https://platform.openai.com/docs/guides/safety-best-practices/end-user-ids">End-User IDs</a>
     */
    private String user;
}
