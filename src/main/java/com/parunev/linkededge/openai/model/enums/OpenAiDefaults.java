package com.parunev.linkededge.openai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: Enumeration of default values for OpenAI integration.
 * <p>
 * This enum defines default values used in the OpenAI integration, such as the default user, default API URL,
 * and default model. It simplifies the configuration process and provides convenient access to these defaults.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Getter
@RequiredArgsConstructor
public enum OpenAiDefaults {

    /**
     * Default user for OpenAI operations.
     */
    DEFAULT_USER("user"),

    /**
     * Default URL for OpenAI API requests.
     */
    DEFAULT_URL("https://api.openai.com/v1/chat/completions"),

    /**
     * Default OpenAI model to use.
     */
    DEFAULT_MODEL(OpenAiModel.GPT_3_5_TURBO.getName());

    /**
     * The value associated with each default.
     */
    private final String value;
}
