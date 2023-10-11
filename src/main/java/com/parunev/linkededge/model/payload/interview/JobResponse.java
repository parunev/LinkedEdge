package com.parunev.linkededge.model.payload.interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    List<CoachingResponse> coaching;
    PreparationResponse preparationResponse;
}
