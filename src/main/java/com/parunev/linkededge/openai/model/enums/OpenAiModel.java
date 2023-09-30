package com.parunev.linkededge.openai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenAiModel {
    GPT_4("gpt-4"), // currently not available
    GPT_3_5_TURBO("gpt-3.5-turbo");

    private final String name;
}
