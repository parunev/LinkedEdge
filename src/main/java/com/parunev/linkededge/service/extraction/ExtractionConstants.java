package com.parunev.linkededge.service.extraction;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum that provides constants used in profile and job extraction services.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Getter
@AllArgsConstructor
public enum ExtractionConstants {

    /**
     * Constant representing the URL for profile retrieval using the "profile_link" query parameter.
     */
    PROFILE_RETRIEVAL_URL("https://api.lix-it.com/v1/person?profile_link="),

    /**
     * Constant representing the URL for job retrieval using the "job_id" query parameter.
     */
    JOB_RETRIEVAL_URL("https://api.lix-it.com/v1/enrich/job?job_id="),

    /**
     * Default value for missing or unspecified data.
     */
    NOT_PRESENT("DATA NOT PRESENT"),
    NAME("name"),
    LOCATION("location"),
    DESCRIPTION("description"),
    IMAGE("img"),
    SALES_NAV_LINK("salesNavLink"),
    ACCOUNT_LINK("link"),
    INSTITUTION_NAME("institutionName"),
    DEGREE("degree"),
    FIELD_OF_STUDY("fieldOfStudy"),
    DATE_STARTED("dateStarted"),
    DATE_ENDED("dateEnded");
    private final String value;
}
