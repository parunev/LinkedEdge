package com.parunev.linkededge.openai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: Data class representing a message in a chat conversation for OpenAI chat completions.
 * <p>
 * This class serves as a data structure to represent a message within a chat conversation for use with OpenAI's chat completion functionality.
 * It includes information about the message role (e.g., "system", "user", or "assistant") and the content of the message.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiMessage {

    /**
     * The role of the message (e.g., "system", "user", or "assistant").
     */
    @JsonProperty(value = "role")
    public String role;

    /**
     * The content of the message.
     */
    @JsonProperty(value = "content")
    public String content;
}
