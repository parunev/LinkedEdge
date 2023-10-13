package com.parunev.linkededge.model.payload.interview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Request payload for preparing for a specific job application.")
public class JobRequest {

    @NotBlank(message = "Please enter the LinkedIn Job you wish to apply for!")
    @Pattern(regexp = "https://www\\.linkedin\\.com/jobs/.*", message = "Not a valid LinkedIn Job Post link")
    @Schema(name = "LinkedIn Job Post link for the job preparation", example = "https://www.linkedin.com/jobs/collections/recommended/?currentJobId=3736393852",
            type = "String")
    private String jobLink;
}
