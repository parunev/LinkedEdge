package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "Request payload for generating random interview questions based on user information.")
public class QuestionRequest {

    @NotNull(message = "Experience is required. Please provide your experience.")
    @Schema(name = "Unique identifier for the user's experience", example = "c4a8cf7-9efc-4a62-9a0d-2b27d51c64ab", type = "UUID")
    private UUID experience;

    @NotNull(message = "Education is required. Please provide your education.")
    @Schema(name = "Unique identifier for the user's education", example = "a3b4df9-5h62-8f8k-3g1j-1b26g74d12bs", type = "UUID")
    private UUID education;

    @NotNull(message = "Skills are required. Please provide at least 5 skills.")
    @Size(max = 5, message = "Please provide at least 1 maximum 5 skills")
    @Schema(name = "List of unique identifiers for user's skills", example = "[\"s2d4df9-5h62-8f8k-3g1j-1b26g74d12bs\", \"d8k4df9-9h12-6f7k-3g1j-2b36g74d12bt\"]",
    type = "List of UUIDs")
    private List<UUID> skills;

    @NotNull(message = "Difficulty is required. Please select a difficulty level (EASY, MODERATE, HARD, EXPERT).")
    @Schema(name = "Difficulty level for the generated questions", example = "EASY", allowableValues = {"EASY", "MODERATE", "HARD", "EXPERT"}
    ,description = "Equals Ignore Case")
    private QuestionDifficulty difficulty;

}
