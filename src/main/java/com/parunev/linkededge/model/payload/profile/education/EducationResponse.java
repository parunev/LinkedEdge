package com.parunev.linkededge.model.payload.profile.education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationResponse {
    private String institutionName;
    private String degree;
    private String fieldOfStudy;
}
