package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
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
@Schema(name = "Response payload for generated interview questions.")
public class QuestionResponse {

    @Schema(name = "Unique identifier for the generated question", example = "a5c3df7-9efc-2a62-1a0d-7b27d51c61ab", type = "UUID")
    private UUID questionId;
    @Schema(name = "Value of the skill related to the question", example = "Java", type = "String")
    private String skillValue;
    @Schema(name = "Difficulty level of the question", example = "EASY", type = "ENUM")
    private QuestionDifficulty difficulty;
    @Schema(name = "The generated question", example = "Explain the concept of object-oriented programming.", type = "String")
    private String questionValue;
    @Schema(name = "The generated example answer", example = "Useful to know because of...", type = "String")
    private String exampleAnswer;
}
