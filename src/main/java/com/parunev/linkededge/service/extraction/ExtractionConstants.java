package com.parunev.linkededge.service.extraction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExtractionConstants {

    PROFILE_RETRIEVAL_URL("https://api.lix-it.com/v1/person?profile_link="),
    JOB_RETRIEVAL_URL("https://api.lix-it.com/v1/enrich/job?job_id="),
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
