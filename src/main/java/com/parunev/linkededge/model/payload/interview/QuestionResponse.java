package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private UUID questionId;
    private String skillValue;
    private QuestionDifficulty difficulty;
    private String questionValue;
    private String exampleAnswer;
}
