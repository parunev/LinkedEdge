package com.parunev.linkededge.openai;

import com.parunev.linkededge.model.Skill;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAiPrompts {

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";

    public static final OpenAiMessage SYSTEM_INTERVIEW_QUESTION_PROMPT =
            OpenAiMessage.builder()
            .role(ROLE_SYSTEM)
            .content("""
            I want you to act like a recruiter and generate interview questions.
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
            The response should be in JSON format and contain an array of questions, each with fields for 'difficulty,' 'skill,' 'question,' and 'answer.'
            Generate the following:
            - Number of Questions: 5 open-ended questions per skill.
            - Clarity: Ensure that the questions are clear and concise.
            - Include Example Answers: Provide specific example answers for each question.""")
            .build();

    public static OpenAiMessage userInterviewQuestionsPrompt(String education, String experience, List<Skill> skills, QuestionDifficulty difficulty){
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content("""
                       Generate 5 open-ended interview questions for each skill with the following criteria:
                        - Education: %s
                        - Experience: %s
                        - Skills (with endorsements):
                          1. %s (Endorsement: %d)
                          2. %s (Endorsement: %d)
                          3. %s (Endorsement: %d)
                          4. %s (Endorsement: %d)
                          5. %s (Endorsement: %d)
                        - Difficulty Level: %s""".formatted(education, experience,
                        skills.get(0).getName(), skills.get(0).getNumOfEndorsement(),
                        skills.get(1).getName(), skills.get(1).getNumOfEndorsement(),
                        skills.get(2).getName(), skills.get(2).getNumOfEndorsement(),
                        skills.get(3).getName(), skills.get(3).getNumOfEndorsement(),
                        skills.get(4).getName(), skills.get(4).getNumOfEndorsement(),
                        difficulty.toString()))
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
