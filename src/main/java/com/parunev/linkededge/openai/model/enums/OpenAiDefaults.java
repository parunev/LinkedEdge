package com.parunev.linkededge.openai.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenAiDefaults {

    DEFAULT_USER("user"),
    DEFAULT_URL("https://api.openai.com/v1/chat/completions"),
    DEFAULT_MODEL(OpenAiModel.GPT_3_5_TURBO.getName());

    private final String value;
}
