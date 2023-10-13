package com.parunev.linkededge.model.payload.profile.experience;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Request payload for manually adding an experience to the user's profile.")
public class ProfileExperienceRequest {

    @NotBlank(message = "Provide small description about your experience, so we can understand it")
    @Schema(name = "Description of the experience", example = "I've been developing AI applications. What have I followed....",
    type = "String")
    private String description;
    @NotBlank(message = "Provide your title, so our questions will match your position")
    @Schema(name = "Job title", example = "Software Developer", type = "String")
    private String title;

    @Schema(name = "Location of the experience", example = "Bulgaria, Sofia", type = "String")
    private String location;

    @Schema(name = "Name of the organisation", example = "LinkedEdge", type = "String")
    private String organisationName;

    @Schema(name = "Date when the experience has started", example = "Nov 2023 / 2022-01-15", type = "String")
    private String dateStarted;

    @Schema(name = "Date when the experience has ended", example = "Nov 2023 / 2022-01-15 / Present", type = "String")
    private String dateEnded;
}
