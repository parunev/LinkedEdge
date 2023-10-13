package com.parunev.linkededge.openai;

import com.parunev.linkededge.model.Skill;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.util.LELogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: This class provides a set of system and user prompts for interacting with an AI assistant specialized
 * in generating interview questions, answering specialized interview questions, and validating skills, education, and job experience.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAiPrompts {

    // Constants for message roles
    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";
    private static final LELogger leLogger = new LELogger(OpenAiPrompts.class);

    /**
     * System message instructing the AI to generate interview questions for various skills.
     */
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
            The response should contain an array of questions in JSON format, with fields for 'difficulty', 'skill', 'question', and 'answer.'
            
            Instructions:
            - Generate 5 open-ended questions per skill.
            - Ensure questions are clear and concise.
            - Provide specific example answers for each question.
            """)
            .build();

    /**
     * User message specifying criteria for generating interview questions.
     */
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

    /**
     * System message instructing the AI to generate a detailed answer to a specialized interview question.
     */
    public static final OpenAiMessage SYSTEM_ANSWER_SPECIALIZED_INTERVIEW_QUESTION_PROMPT =
            OpenAiMessage.builder()
                    .role(ROLE_SYSTEM)
                    .content("""
        Please provide a detailed answer to a specialized interview question in JSON format. Include an example and explain why knowing this topic is beneficial in an interview.
        
        Response Schema:
        - Title: Specialized Interview Question Answer
        - Format: JSON
        - Content Structure:
          {
            "answer": "",
            "example": "",
            "benefits": ""
          }
        The response should be in JSON format, with fields for 'answer', 'example', and 'benefits'
        
        Instructions:
        - Do not provide answers or generate content unrelated to professional job interview questions.
        - Do not provide answers for any content that is not related to interviews.
        - Do not include any content related to religion, financial status, relationships, gender, hobbies, or personal user information.
        - Provide a comprehensive answer to a specialized interview question.
        - Include a real-world example to illustrate your response.
        - Explain why understanding this topic is advantageous for a candidate in an interview.
        - The question may cover a challenging topic related to various skills.
        """).build();

    /**
     * User message for generating a specialized answer to an interview question.
     */
    public static OpenAiMessage userGenerateSpecializedAnswer(String question) {
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content(("Imagine you are in an interview, and the interviewer asks you the following question: \"%s?\".%n" +
                        "Now, provide an exceptionally creative and unique response that nobody has ever heard before.").formatted(question))
                .build();
    }

    /**
     * System message instructing the AI to validate whether a skill is valid or not.
     */
    public final static OpenAiMessage SYSTEM_IS_IT_VALID_SKILL =
        OpenAiMessage.builder()
                .role(ROLE_SYSTEM)
                .content("""
            I want you to act like a validation system for skills.
            The response should be a single word 'yes' or 'no' in lower case
            """)
                .build();

    /**
     * User message for validating a skill.
     */
    public static OpenAiMessage userIsItValidSkill(String skillToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + skillToCheck + "' a valid or can be considered as one skill?")
                .build();
    }

    /**
     * System message instructing the AI to validate whether an education institution is valid or not.
     */
    public final static OpenAiMessage SYSTEM_IS_IT_VALID_EDUCATION =
            OpenAiMessage.builder()
                    .role(ROLE_SYSTEM)
                    .content("""
            I want you to act like a validation system for education institutions.
            The response should be a single word 'yes' or 'no' in lower case
            """)
                    .build();

    /**
     * User message for validating an education institution.
     */
    public static OpenAiMessage userIsItValidEducation(String educationToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + educationToCheck + "' a valid education institution?")
                .build();
    }


    /**
     * System message instructing the AI to validate whether a job experience is valid or not.
     */
    public final static OpenAiMessage SYSTEM_IS_IT_VALID_EXPERIENCE =
            OpenAiMessage.builder()
                    .role(ROLE_SYSTEM)
                    .content("""
                             I want you to act as a validation system for job experience.
                             Provide the job title and description, and I will determine if it's a valid job experience.
                             The response should be a single word 'yes' or 'no' in lower case.
                            """)
                    .build();

    /**
     * User message for validating a job experience.
     */
    public static OpenAiMessage userIsItValidExperience(String experienceToCheck){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("Is '" + experienceToCheck + "' a valid experience?")
                .build();
    }
}
