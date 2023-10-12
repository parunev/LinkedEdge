package com.parunev.linkededge.model.payload.profile.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Request payload for manually adding a skill to the user's profile.")
public class ProfileSkillRequest {

    @NotBlank(message = "Give your skill a meaningful name.")
    @Schema(name = "Name of the skill", example = "Java17", type = "String")
    private String name;

    @NotBlank(message = "Provide the level of experience you think you have")
    @Min(value = 1, message = "If your level of experience is less than 1 we suggest to not enter this skill")
    @Max(value = 10, message = "Our maximum skill level currently is 10.")
    @Schema(name = "User's self-esteemed skill level (1-10)", example = "7", description = "Even though the numOfEndorsements doesn't" +
            "work like that in LinkedIn we still need to collect as many important information as possible. **This could be edited!**")
    private String numOfEndorsement;
}
