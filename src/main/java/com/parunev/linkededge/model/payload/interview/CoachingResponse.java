package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachingResponse {

    private QuestionDifficulty difficulty;
    private String question;
    private String insight;
    private String advice;
    private String answer;
}
