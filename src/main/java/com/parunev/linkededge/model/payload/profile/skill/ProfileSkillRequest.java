package com.parunev.linkededge.model.payload.profile.skill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileSkillRequest {

    @NotBlank(message = "Give your skill a meaningful name.")
    private String name;

    @NotBlank(message = "Provide the level of experience you think you have")
    @Min(value = 1, message = "If your level of experience is less than 1 we suggest to not enter this skill")
    @Max(value = 10, message = "Our maximum skill level currently is 10.")
    private String numOfEndorsement;
}
