package com.parunev.linkededge.model.payload.profile.education;

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
@Schema(name = "Response payload for a successfully added education to the user's profile.")
public class EducationResponse {

    @Schema(name = "Unique identifier for the added education", example = "e4a8cf7-9efc-4a62-9a0d-2b27d51c64ab", type = "UUID")
    private UUID educationId;
    @Schema(name = "Name of the educational institution", example = "LinkedEdge University", type = "String")
    private String institutionName;
    @Schema(name = "Degree obtained", example = "Bachelor of Science", type = "String")
    private String degree;
    @Schema(name = "Field of study or major", example = "Computer Science", type = "String")
    private String fieldOfStudy;
}
