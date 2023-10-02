package com.parunev.linkededge.model.payload.profile.education;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationResponse {
    private UUID educationId;
    private String institutionName;
    private String degree;
    private String fieldOfStudy;
}
