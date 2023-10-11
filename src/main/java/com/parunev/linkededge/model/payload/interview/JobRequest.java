package com.parunev.linkededge.model.payload.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Please enter the LinkedIn Job you wish to apply for!")
    @Pattern(regexp = "https://www\\.linkedin\\.com/jobs/.*", message = "Not a valid LinkedIn Job Post link")
    private String jobLink;
}
