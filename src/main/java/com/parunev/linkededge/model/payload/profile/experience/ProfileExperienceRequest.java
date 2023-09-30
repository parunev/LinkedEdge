package com.parunev.linkededge.model.payload.profile.experience;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileExperienceRequest {

    @NotBlank(message = "Provide small description about your experience, so we can understand it")
    private String description;
    @NotBlank(message = "Provide your title, so our questions will match your position")
    private String title;
    private String location;
    private String organisationName;
    private String dateStarted;
    private String dateEnded;
}
