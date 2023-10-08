package com.parunev.linkededge.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobType {
    FULL_TIME("I'm looking for a full-time position"),
    PART_TIME("I prefer part-time work to balance with other commitments"),
    CONTRACT("I'm open to contract-based opportunities"),
    TEMPORARY("I want a temporary position for a specific project"),
    VOLUNTEER("I'm interested in volunteering for a cause"),
    INTERNSHIP("I want to join an internship program to gain experience");

    private final String jobTypeValue;
}
