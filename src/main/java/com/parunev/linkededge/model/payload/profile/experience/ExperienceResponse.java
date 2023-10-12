package com.parunev.linkededge.model.payload.profile.experience;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response payload for a successfully added experience to the user's profile.")
public class ExperienceResponse {

    @Schema(name = "Unique identifier for the added experience", example = "c4a8cf7-9efc-4a62-9a0d-2b27d51c64ab", type = "UUID")
    private UUID experienceId;

    @Schema(name = "Description of the added experience", type = "String")
    private String description;

    @Schema(name = "Job title of the added experience", example = "Software Developer", type = "String")
    private String title;
}
