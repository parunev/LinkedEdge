package com.parunev.linkededge.model.payload.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import com.parunev.linkededge.model.payload.interview.QuestionResponse;
import com.parunev.linkededge.model.payload.profile.education.EducationResponse;
import com.parunev.linkededge.model.payload.profile.experience.ExperienceResponse;
import com.parunev.linkededge.model.payload.profile.skill.SkillResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload for the user's profile, including education, experience, skills, and interview questions.")
public class ProfileResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "List of user's education details")
    private List<EducationResponse> educations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "List of user's experience details")
    private List<ExperienceResponse> experiences;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "List of user's skills")
    private List<SkillResponse> skills;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "List of interview questions and responses")
    private List<QuestionResponse> questions;
}
