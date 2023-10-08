package com.parunev.linkededge.model.payload.profile;

import com.parunev.linkededge.model.enums.ExperienceLevel;
import com.parunev.linkededge.model.enums.JobType;
import com.parunev.linkededge.model.enums.WorkPlace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Please enter the LinkedIn Job you wish to apply for!")
    @Pattern(regexp = "https://www\\.linkedin\\.com/jobs/.*", message = "Not a valid LinkedIn Job Post link")
    private String jobLink;

    @NotBlank(message = "Please choose your workplace type. Options are: REMOTE, ONSITE, HYBRID.")
    private WorkPlace workPlace;

    @NotBlank(message = "Please choose your experience level. Options are: INTERNSHIP, ENTRY_LEVEL, ASSOCIATE, MID_SENIOR_LEVEL, DIRECTOR, EXECUTIVE.")
    private ExperienceLevel experienceLevel;

    @NotBlank(message = "Please enter your job employment desire. Options are: FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, VOLUNTEER, INTERNSHIP.")
    private JobType jobType;

    private String currentJobPosition;
}
