package com.parunev.linkededge.openai;

import com.parunev.linkededge.model.Skill;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.util.LELogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAiPrompts {

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";
    private static final LELogger leLogger = new LELogger(OpenAiPrompts.class);

    public static final OpenAiMessage SYSTEM_INTERVIEW_QUESTION_PROMPT =
            OpenAiMessage.builder()
            .role(ROLE_SYSTEM)
            .content("""
            I want you to act like a recruiter and generate interview questions in JSON format for various skills.
            
            Response Schema:
            - Title: Interview Questions
            - Format: JSON
            - Content Structure:
              {
                "questions": [
                  {
                    "difficulty": "",
                    "skill": "",
                    "question": "",
                    "answer": ""
                  },
                  {
                    "difficulty": "",
                    "skill": "",
                    "question": "",
                    "answer": ""
                  },
                  ...
                ]
              }
            The response should contain an array of questions in JSON format, with fields for 'difficulty,' 'skill,' 'question,' and 'answer.'
            
            Instructions:
            - Generate 5 open-ended questions per skill.
            - Ensure questions are clear and concise.
            - Provide specific example answers for each question.
            """)
            .build();

    public static OpenAiMessage userInterviewQuestionsPrompt(String education, String experience, List<Skill> skills, QuestionDifficulty difficulty){
        StringBuilder content = new StringBuilder();
        content.append("Generate 5 open-ended interview questions for each skill with the following criteria:\n");
        content.append("- Education: %s%n".formatted(education));
        content.append("- Experience: %s%n".formatted(experience));
        content.append("- Skills (with endorsements):\n");

        for (int i = 1; i <= skills.size(); i++) {
            content.append("%d. %s (Endorsement: %d)%n".formatted(i
                    ,skills.get(i-1).getName()
                    ,skills.get(i-1).getNumOfEndorsement()));
        }
        content.append("- Difficulty Level: %s".formatted(difficulty.toString()));

        leLogger.debug("Prompt: {}", content.toString());

        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content(content.toString())
                .build();
    }

    public final static OpenAiMessage SYSTEM_IS_IT_VALID_SKILL =
        OpenAiMessage.builder()
                .role(ROLE_SYSTEM)
                .content("""
            I want you to act like a validation system for skills.
            The response should be a single word 'yes' or 'no' in lower case
            """)
                .build();

    public static OpenAiMessage userIsItValidSkill(String skillToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + skillToCheck + "' a valid or can be considered as one skill?")
                .build();
    }

    public final static OpenAiMessage SYSTEM_IS_IT_VALID_EDUCATION =
            OpenAiMessage.builder()
                    .role(ROLE_SYSTEM)
                    .content("""
            I want you to act like a validation system for education institutions.
            The response should be a single word 'yes' or 'no' in lower case
            """)
                    .build();

    public static OpenAiMessage userIsItValidEducation(String educationToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + educationToCheck + "' a valid education institution?")
                .build();
    }

    public final static OpenAiMessage SYSTEM_IS_IT_VALID_EXPERIENCE =
            OpenAiMessage.builder()
                    .role(ROLE_SYSTEM)
                    .content("""
                             I want you to act as a validation system for job experience.
                             Provide the job title and description, and I will determine if it's a valid job experience.
                             The response should be a single word 'yes' or 'no' in lower case.
                            """)
                    .build();

    public static OpenAiMessage userIsItValidExperience(String experienceToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + experienceToCheck + "' a valid experience?")
                .build();
    }
}
