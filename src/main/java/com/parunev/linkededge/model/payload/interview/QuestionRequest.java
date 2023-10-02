package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionRequest {

    @NotNull(message = "Experience is required. Please provide your experience.")
    private UUID experience;

    @NotNull(message = "Education is required. Please provide your education.")
    private UUID education;

    @NotNull(message = "Skills are required. Please provide at least 5 skills.")
    @Size(max = 5, message = "Please provide at least 1 maximum 5 skills")
    private List<UUID> skills;

    @NotNull(message = "Difficulty is required. Please select a difficulty level (EASY, MODERATE, HARD, EXPERT).")
    private QuestionDifficulty difficulty;

}
