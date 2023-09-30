package com.parunev.linkededge.model.payload.profile.education;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileEducationRequest {

    @NotBlank(message = "Institution name is need in order to add Education to your profile")
    private String institutionName;
    @NotBlank(message = "Degree is needed in order to add Education to your profile")
    private String degree;
    @NotBlank(message = "Field of study is needed in order to add Education to your profile")
    private String fieldOfStudy;
    private String dateStarted;
    private String dateEnded;
}
