package com.parunev.linkededge.model.payload.profile.education;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Request payload for manually adding education details to the user's profile.")
public class ProfileEducationRequest {

    @NotBlank(message = "Institution name is need in order to add Education to your profile")
    @Schema(name = "Name of the educational institution", example = "LinkedEdge University", type = "String")
    private String institutionName;
    @NotBlank(message = "Degree is needed in order to add Education to your profile")
    @Schema(name = "Degree obtained", example = "Bachelor of Science / Certified at (etc.)", type = "String")
    private String degree;
    @NotBlank(message = "Field of study is needed in order to add Education to your profile")
    @Schema(name = "Field of study or major", example = "Computer Science", type = "String")
    private String fieldOfStudy;
    @Schema(name = "Date when the education started", example = "Oct 2023 / 2018-09-01")
    private String dateStarted;
    @Schema(name = "Date when the education ended", example = "Oct 2023 / 2018-09-01 / Present")
    private String dateEnded;
}
