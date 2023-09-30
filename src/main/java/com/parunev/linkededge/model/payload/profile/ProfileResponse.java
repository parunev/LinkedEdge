package com.parunev.linkededge.model.payload.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import com.parunev.linkededge.model.payload.profile.education.EducationResponse;
import com.parunev.linkededge.model.payload.profile.experience.ExperienceResponse;
import com.parunev.linkededge.model.payload.profile.skill.SkillResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ProfileResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EducationResponse> educations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExperienceResponse> experiences;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SkillResponse> skills;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<QuestionResponse> questions;
}
