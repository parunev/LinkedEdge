package com.parunev.linkededge.openai.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: Payload class for representing the response from OpenAI's chat completion requests.
 * <p>
 * This class is used to encapsulate the response data from chat completion requests to OpenAI's language models.
 * It includes information about the generated completion, such as ID, creation timestamp, model used, choices, and usage statistics.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCompletionResponse {

    /**
     * The unique identifier for the completion.
     */
    @JsonProperty(value = "id")
    public String id;

    /**
     * The type of object (e.g., "chat.completion").
     */
    @JsonProperty(value = "object")
    public String object;

    /**
     * The timestamp when the completion was created.
     */
    @JsonProperty(value = "created")
    public Long created;

    /**
     * The model used for generating the completion.
     */
    @JsonProperty(value = "model")
    public String model;

    /**
     * A list of choices, each representing a potential completion.
     */
    @JsonProperty(value = "choices")
    public List<Choice> choices;

    /**
     * Information about token usage statistics.
     */
    @JsonProperty(value = "usage")
    public Usage usage;

    /**
     * A nested class to represent a choice in the response.
     */
    @Data
    public static class Choice {

        /**
         * The index of the choice.
         */
        @JsonProperty(value = "index")
        public Integer index;

        /**
         * The generated message in the choice.
         */
        @JsonProperty(value = "message")
        public OpenAiMessage message;

        /**
         * The reason for finishing the choice (e.g., "stop" or "max_tokens").
         */
        @JsonProperty(value = "finish_reason")
        public String finishReason;
    }

    /**
     * A nested class to represent token usage statistics.
     */
    @Data
    public static class Usage {
        /**
         * The number of tokens used for the prompt.
         */
        @JsonProperty(value = "prompt_tokens")
        public Integer promptTokens;

        /**
         * The number of tokens used for the completion.
         */
        @JsonProperty(value = "completion_tokens")
        public Integer completionTokens;

        /**
         * The total number of tokens used in the response.
         */
        @JsonProperty(value = "total_tokens")
        public Integer totalTokens;
    }
}
