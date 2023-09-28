package com.parunev.linkededge.service.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileConstants {

    PROFILE_RETRIEVAL_URL("https://api.lix-it.com/v1/person?profile_link="),
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
