package com.parunev.linkededge.openai;

import com.parunev.linkededge.model.Education;
import com.parunev.linkededge.model.Experience;
import com.parunev.linkededge.model.Organisation;
import com.parunev.linkededge.model.Skill;
import com.parunev.linkededge.model.job.CompanyResolution;
import com.parunev.linkededge.model.job.CompanySpecialty;
import com.parunev.linkededge.model.job.JobFunction;
import com.parunev.linkededge.model.job.JobIndustry;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.util.LELogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenAiJobPrompt {

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_USER = "user";
    private static final LELogger leLogger = new LELogger(OpenAiPrompts.class);

    public final static OpenAiMessage SYSTEM_PREPARE_AND_COACH_FOR_INTERVIEW = OpenAiMessage.builder()
            .role(ROLE_SYSTEM)
            .content("""
                    I want you to act like 'Interview Preparation and Coaching Assistant' and generate highly personalized
                    and tailored interview preparation guidance.
                    
                    Response Schema:
            - Title: Coaching for Interview
            - Format: JSON
            - Content Structure:
              {
                "preparation": [
                  {
                  "doYouFit": "",
                  "doYouNotFit": ""
                  }
                ],
                "coaching": [
                  {
                    "difficulty": "",
                    "question": "",
                    "insight": "",
                    "advice": "",
                    "answer": ""
                  },
                  {
                    "difficulty" "",
                    "question": "",
                    "insight": "",
                    "advice": "",
                    "answer": ""
                  },
                  ...
                ]
              }
            The response should contain two arrays one for preparation and for for coaching in JSON format.
            First 'preparation' array will have two fields: 'doYouFit', 'doYouNotFit' which are both required.
            Second 'coaching' array will have five fields: 'difficulty', 'question', 'insight', 'advice', and 'answer'.
                    
                    Instructions:
                    - Do not provide answers or generate content unrelated to professional job interview questions.
                    - Do not provide answers for any content that is not related to interviews.
                    - Generate 5 or more clear and concise open-ended questions.
                    - Provide information why the person is a good fit at 'doYouFit' field
                    - Provide information why the person is not a good fit at 'doYouNotFit' field
                    - Provide difficulties to each questions ('EASY', 'MODERATE', 'HARD', 'EXPERT')
                    - Provide insights that explain the importance or relevance of each question for better interview preparation.
                    - Provide precise and customized approach to the interview questions.
                    - Provide a comprehensive real-life example answer to each specialized interview question.
                    - The question may cover a challenging topic related to various skills.
                    """)
            .build();

    public static OpenAiMessage userPrepareAndCoachForInterview(String companyName, Integer companyStaff,
                                                                List<CompanySpecialty> companySpecialties, List<CompanyResolution> companyResolutions,
                                                                String jobDescription, String employmentStatus,
                                                                String jobTitle, List<JobFunction> jobFunctions,
                                                                List<JobIndustry> jobIndustries, String personDescription,
                                                                List<Organisation> personRelatedOrganisation, List<Education> personEducation,
                                                                List<Experience> personExperience, List<Skill> personSkills){

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("I'm seeking an interview preparation and coaching for a job at %s.%n".formatted(companyName));
        promptBuilder.append("The company employs %d staff and specializes in:%n".formatted(companyStaff));

        if (companySpecialties==null){
            promptBuilder.append("I'm not sure at what they specialize. Sorry about that!\n");
        } else {
            for (CompanySpecialty specialty : companySpecialties){
                promptBuilder.append(" -%s.%n".formatted(specialty.getSpecialtyName()));
            }
        }

        promptBuilder.append("The company has set the following resolutions in the");
        if (jobIndustries==null){
            promptBuilder.append("I'm not sure what industries the specialize in. Also sorry about that!\n");
        } else {
            for (JobIndustry industry : jobIndustries){
                promptBuilder.append(" %s".formatted(industry.getJobIndustryName()));
            }
        }

        promptBuilder.append("industries:\n");
        if (companyResolutions==null){
            promptBuilder.append("I'm not quite sure what resolutions they provide. Again sorry!\n");
        } else {
            for (CompanyResolution resolution : companyResolutions){
                promptBuilder.append(" -%s.%n".formatted(resolution.getCompanyDescription()));
            }
        }

        promptBuilder.append("The job description is the following:\n");
        promptBuilder.append("Job Description: %s%n%n".formatted(jobDescription));

        promptBuilder.append("The job description for the position of %s as %s includes the following functions:%n".formatted(jobTitle, employmentStatus));
        if (jobFunctions==null){
            promptBuilder.append("There were not functions presented in the job description, so I will skip this part.\n");
        } else {
            for (JobFunction function : jobFunctions){
                promptBuilder.append(" -%s.%n".formatted(function.getJobFunctionName()));
            }
        }

        promptBuilder.append("Here is a brief description about me:\n");
        promptBuilder.append("%s%n".formatted(personDescription));
        promptBuilder.append("My educational background includes:\n");
        if (personEducation==null){
            promptBuilder.append("Currently I got no education background.\n");
        } else {
            for (Education education : personEducation){
                String degree;
                String fieldOfStudy;
                String institution;

                if (education.getDegree().equals("DATA NOT PRESENT")){
                    degree = "Prefer not to say";
                } else {
                    degree = education.getDegree();
                }

                if (education.getFieldOfStudy().equals("DATA NOT PRESENT")){
                    fieldOfStudy = "Prefer not to say";
                } else {
                    fieldOfStudy = education.getFieldOfStudy();
                }

                if (education.getInstitutionName().equals("DATA NOT PRESENT")){
                    institution = "Prefer not to say";
                } else {
                    institution = education.getInstitutionName();
                }

                promptBuilder.append("Degree: %s, Field of Study: %s, Institution name: %s%n"
                        .formatted(degree, fieldOfStudy, institution));
            }
        }

        promptBuilder.append("I've worked with the following organizations:\n");
        if (personRelatedOrganisation==null){
            promptBuilder.append("I've not worked for any organization, mainly on my own\n");
        } else {
            for (Organisation organisation : personRelatedOrganisation){
                promptBuilder.append(" - %s.%n".formatted(organisation.getName()));
            }
        }

        promptBuilder.append("My past experience include:\n");
        if (personExperience==null){
            promptBuilder.append("I currently got no experiences in any fields. I need to work on that!");
        } else {
            for (Experience experience : personExperience){
                String description;
                if (experience.getDescription() == null){
                    description = "Prefer not to say";
                } else {
                    description = experience.getDescription();
                }

                promptBuilder.append("Date Started: %s, Date Ended: %s, Job Title: %s, Description: %s%n"
                        .formatted(experience.getDateStarted(), experience.getDateEnded(), experience.getTitle(), description));
            }
        }

        promptBuilder.append("My skills include:\n");
        if (personSkills==null){
            promptBuilder.append("I don't know any skills of mine, so I didn't pass any here either. Sorry for that!");
        } else {
            for (Skill skill : personSkills){
                promptBuilder.append("- %s.%n".formatted(skill.getName()));
            }
        }

        promptBuilder.append("Please provide highly personalized and tailored interview preparation guidance based on the provided job description.\n");
        promptBuilder.append("Additionally, I'd like to know why I may be a good fit for the position (in the 'doYouFit' field) and why I might not be a good fit (in the 'doYouNotFit' field).");

        leLogger.info("Prompt: {}", promptBuilder.toString());
        return OpenAiMessage.builder()
                .role(ROLE_USER)
                .content(promptBuilder.toString())
                .build();
    }
}
