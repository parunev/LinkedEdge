package com.parunev.linkededge.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkPlace {
    REMOTE("I prefer to work remotely"),
    ONSITE("I want to at the company's physical office"),
    HYBRID("I'm open to a hybrid work arrangement");

    private final String workPlaceValue;
}
