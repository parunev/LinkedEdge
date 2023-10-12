package com.parunev.linkededge.model.payload.profile.skill;

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
@Schema(name = "Response payload for a successfully added skill to the user's profile.")
public class SkillResponse {

    @Schema(name = "Unique identifier for the added skill", example = "ec4a8cf7-9efc-4a62-9a0d-2b27d51c64ab", type = "UUID")
    private UUID skillId;
    @Schema(name = "Name of the added skill", example = "Java", type = "String")
    private String name;

    @Schema(name = "User's self-esteemed skill level (1-10) or the endorsements coming from the LinkedIn profile",
    example = "10", type = "String")
    private String numOfEndorsement;
}
