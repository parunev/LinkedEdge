package com.parunev.linkededge.model.payload.interview;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Response payload for providing coaching insights and advice for interview questions.")
public class CoachingResponse {

    @Schema(name = "Difficulty level of the related interview question", example = "MODERATE", type = "ENUM")
    private QuestionDifficulty difficulty;
    @Schema(name = "The interview question", example = "How do you ensure code quality in your development process?", type = "String")
    private String question;
    @Schema(name = "Insights or recommendations for answering the question",
            example = "Code quality is crucial for the maintainability, reliability, and scalability of software systems. Ensuring code quality involves various practices and tools that help identify and prevent issues, enforce coding standards, and promote best practices.",
            type = "String")
    private String insight;
    @Schema(name = "Advice for providing an effective response", example = "Give concrete examples from past projects.", type = "String")
    private String advice;

    @Schema(name = "Answer to the question for reference", example = "To answer this question, you can discuss the different aspects of code quality assurance, such as code reviews, automated testing, and static code analysis. Explain how you collaborate with team members to review code and provide constructive feedback. Mention the use of unit tests, integration tests, and continuous integration tools to ensure functional correctness. Discuss the importance of following coding standards and using tools like SonarQube or Checkstyle to enforce them. Additionally, highlight the value of refactoring and continuous improvement in maintaining code quality.",
    type = "String")
    private String answer;
}
