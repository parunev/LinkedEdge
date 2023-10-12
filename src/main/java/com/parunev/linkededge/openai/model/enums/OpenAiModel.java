package com.parunev.linkededge.openai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description: Enumeration of OpenAI language models.
 * <p>
 * This enum defines language models available for use with OpenAI. It provides an easy way to specify the desired model
 * for generating natural language text. Currently, it includes the "gpt-3.5-turbo" model (GPT-3.5 Turbo) and a "gpt-4" model
 * (which is currently not available).
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Getter
@RequiredArgsConstructor
public enum OpenAiModel {

    /**
     * GPT-4 model (currently not available).
     */
    GPT_4("gpt-4"),

    /**
     * GPT-3.5 Turbo model.
     */
    GPT_3_5_TURBO("gpt-3.5-turbo");

    /**
     * The name of the OpenAI language model.
     */
    private final String name;
}
