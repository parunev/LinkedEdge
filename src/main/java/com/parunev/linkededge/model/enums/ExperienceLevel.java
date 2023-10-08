package com.parunev.linkededge.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExperienceLevel {
    INTERNSHIP("I want to join the job because I'm internship type"),
    ENTRY_LEVEL("I'm just starting my career and looking for entry-level positions"),
    ASSOCIATE("I have some experience and looking for an associate-level role"),
    MID_SENIOR_LEVEL("I'm experienced professional seeking mid-senior level position"),
    DIRECTOR("I have a leadership role and want to join as a director"),
    EXECUTIVE("I'm a top-level executive looking for executive positions");

    private final String experienceValue;
}
