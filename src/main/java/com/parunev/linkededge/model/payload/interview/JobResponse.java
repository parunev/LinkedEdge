package com.parunev.linkededge.model.payload.interview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response payload for job interview insights and preparation recommendations.")
public class JobResponse {

    @Schema(name = "List of coaching responses for interview questions", type = "List of questions, answer, insights")
    List<CoachingResponse> coaching;

    @Schema(name = "Interview preparation insight of how do you fit")
    PreparationResponse preparationResponse;
}
