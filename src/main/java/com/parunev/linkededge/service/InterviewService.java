package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.model.payload.interview.AnswerRequest;
import com.parunev.linkededge.model.payload.interview.AnswerResponse;
import com.parunev.linkededge.model.payload.interview.QuestionRequest;
import com.parunev.linkededge.model.payload.interview.QuestionResponse;
import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.UserProfileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.parunev.linkededge.openai.OpenAiPrompts.*;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@Service
@Validated
@RequiredArgsConstructor
public class InterviewService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final OrganisationRepository organisationRepository;
    private final QuestionRepository questionRepository;
    private final SpecializedAnswerRepository specializedAnswerRepository;
    private final UserProfileUtils upUtils;
    private final OpenAi openAi;
    private final ModelMapper modelMapper;
    private final LELogger leLogger = new LELogger(InterviewService.class);


    public AnswerResponse answerUserQuestion(@Valid AnswerRequest request){
        Pair<User, Profile> pair = upUtils.getUserAndProfile();

        checkForCreditAvailability(pair.getRight().getCredits());
        pair.getRight().setCredits(pair.getRight().getCredits() - 1);

        AnswerResponse response;
        try{
            response = generateAnswerForUserQuestion(request);
        } catch (JSONException e) {
            throw new InvalidExtractException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Either nothing was extract or the operation " +
                            "was aborted due to inappropriate or unrelated question.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        if (response.getExample().equals("") || response.getBenefits().equals("")){
            throw new UserProfileException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Operation was aborted due to inappropriate or unrelated question.")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        SpecializedAnswer answer = SpecializedAnswer.builder()
                .profile(pair.getRight())
                .question(response.getQuestion())
                .answer(response.getAnswer())
                .example(response.getExample())
                .benefits(response.getBenefits())
                .build();

        profileRepository.save(pair.getRight());
        specializedAnswerRepository.save(answer);

        return response;
    }

    private AnswerResponse generateAnswerForUserQuestion(AnswerRequest request) throws JSONException {
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(SYSTEM_ANSWER_SPECIALIZED_INTERVIEW_QUESTION_PROMPT);
        messages.add(userGenerateSpecializedAnswer(request.getQuestion()));
        String answer = openAi.ask(messages);

        JSONObject jsonObject = new JSONObject(answer);

        return AnswerResponse.builder()
                .question(request.getQuestion())
                .answer(jsonObject.getString("answer"))
                .example(jsonObject.getString("example"))
                .benefits(jsonObject.getString("benefits"))
                .build();
    }

    public List<QuestionResponse> generateRandomInterviewQuestions(@Valid QuestionRequest request) {
        Pair<User, Profile> pair = upUtils.getUserAndProfile();

        checkForCreditAvailability(pair.getRight().getCredits());

        Education education = isTheEducationExistingOne(request.getEducation());
        Experience experience = isTheExperienceExistingOne(request.getExperience());
        List<Skill> skills = isTheSkillsExistingOnes(request.getSkills());
        QuestionDifficulty difficulty = request.getDifficulty();

        List<Question> questions;

        try{
             questions = generateInterviewQuestions(pair.getRight(), education, experience, skills, difficulty);
        } catch (Exception e){
            leLogger.error(e.getMessage() + "Exception: {} Cause: {}",e, e.getCause());
            throw new InvalidWritingException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        pair.getRight().setCredits(pair.getRight().getCredits() - 1);
        profileRepository.save(pair.getRight());

        leLogger.info("Questions generating operation successful, returning them to the user");
        return questions.stream()
                .map(question -> modelMapper.map(question, QuestionResponse.class))
                .toList();
    }

    private List<Question> generateInterviewQuestions(Profile profile, Education education, Experience experience, List<Skill> skills, QuestionDifficulty difficulty) throws JSONException {
        leLogger.info("Starting the generation of interview questions");
        String educationPrompt = generateEducatePrompt(education);
        String experiencePrompt = generateExperiencePrompt(experience);

        leLogger.info("Asking open ai to generate the interview questions");
        List<OpenAiMessage> messages = new ArrayList<>();

        messages.add(SYSTEM_INTERVIEW_QUESTION_PROMPT);
        messages.add(userInterviewQuestionsPrompt(
                educationPrompt,
                experiencePrompt,
                skills,
                difficulty
                ));

        String questions = openAi.ask(messages);
        leLogger.info("Questions are ready to be sent to the end-client");
        return returnQuestionsAfterProcessing(profile, education, experience, skills, questions);
    }

    private List<Question> returnQuestionsAfterProcessing(Profile profile, Education education,
                                                          Experience experience, List<Skill> skill, String questions) throws JSONException {
        leLogger.info("Starting the questions processing");
        JSONObject object = new JSONObject(questions);
        JSONArray arrayOfQuestions = object.getJSONArray("questions");

        leLogger.info("Validating the array of questions");
        if (arrayOfQuestions.length() == 0){
            leLogger.warn("No questions were extracted from the response.");
            throw new InvalidExtractException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("No questions were extracted from the response.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        leLogger.info("Extracting and saving each question to the database");
        List<Question> listToReturn = new ArrayList<>();
        for (int i = 0; i < arrayOfQuestions.length(); i++) {
            JSONObject questionObj = arrayOfQuestions.getJSONObject(i);

            String skillName = questionObj.getString("skill");
            Skill matchingSkill = null;
            for (Skill skillItem : skill) {
                if (skillItem.getName().equalsIgnoreCase(skillName)) {
                    matchingSkill = skillItem;
                    break;
                }
            }

            Question question = Question.builder()
                    .skillValue(questionObj.getString("skill"))
                    .difficulty(QuestionDifficulty.valueOf(questionObj.getString("difficulty").toUpperCase()))
                    .questionValue(questionObj.getString("question"))
                    .exampleAnswer(questionObj.getString("answer"))
                    .experience(experience)
                    .education(education)
                    .skill(matchingSkill)
                    .profile(profile)
                    .build();
            questionRepository.save(question);
            listToReturn.add(question);
        }

        leLogger.info("Questions are saved and the list is ready to be returned");
        return listToReturn;
    }

    private String generateExperiencePrompt(Experience experience) {
        StringBuilder message = new StringBuilder();
        Organisation organisation = checkForExperienceOrganisation(experience.getOrganisation().getId());

        if (!experience.getTitle().isEmpty()){
            message.append(experience.getTitle()).append(", ");
            leLogger.debug("Appended Job Title {}", message.toString());
        }

        if (!organisation.getName().isEmpty()){
            message.append(organisation.getName()).append(", ");
            leLogger.debug("Appended Organisation Name {}", message.toString());
        }

        if (experience.getDateStarted() != null && !experience.getDateStarted().isEmpty()){
            message.append(experience.getDateStarted()).append("-");
            leLogger.debug("Appended Experience Date Started {}", message.toString());
        }

        if (experience.getDateEnded() != null && !experience.getDateEnded().isEmpty()){
            message.append(experience.getDateEnded());
            leLogger.debug("Appended Experience Date Ended {}", message.toString());
        }

        leLogger.info("Final Experience Prompt: {}", message.toString());
        return message.toString();
    }

    private String generateEducatePrompt(Education education) {
        StringBuilder message = new StringBuilder();

        if (!education.getInstitutionName().isEmpty()){
            message.append(education.getInstitutionName()).append(", ");
            leLogger.debug("Appended Institution Name: {}", message.toString());
        }

        if (!education.getFieldOfStudy().equals("DATA NOT PRESENT")){
            message.append(education.getFieldOfStudy()).append(",");
            leLogger.debug("Appended Field of Study: {}", message.toString());
        }

        if (!education.getDegree().equals("DATA NOT PRESENT")){
            message.append(education.getDegree()).append(", ");
            leLogger.debug("Appended Degree: {}", message.toString());
        }

        if (!education.getDateStarted().equals("NOT PRESENT")){
            message.append(education.getDateStarted()).append("-");
            leLogger.debug("Appended Education Date Started: {}", message.toString());
        }

        if (!education.getDateEnded().equals("NOT PRESENT")){
            message.append(education.getDateEnded());
            leLogger.debug("Appended Education Date Ended: {}", message.toString());
        }

        leLogger.info("Final Education Prompt: {}", message.toString());
        return message.toString();
    }

    private void checkForCreditAvailability(Integer credits) {
        if (credits == 0){
            leLogger.warn("Insufficient credits.");
            throw new InsufficientCapacityException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Insufficient credits. Consider buying more credits for your profile!")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    private List<Skill> isTheSkillsExistingOnes(List<UUID> skills) {
        if (skills.size() != skillRepository.findAllById(skills).size()){
            leLogger.warn("One or more skills not found.");
            throw new SkillNotFoundException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("One or more skills not found.")
                    .status(HttpStatus.NOT_FOUND)
                    .timestamp(LocalDateTime.now())
                    .build());
        } else {
            return skillRepository.findAllById(skills);
        }
    }

    private Experience isTheExperienceExistingOne(UUID experienceId) {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() ->{
                    leLogger.warn("Experience not found.");
                    throw new ExperienceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Experience not found.")
                            .status(HttpStatus.NOT_FOUND)
                            .timestamp(LocalDateTime.now())
                            .build());
                });
    }

    private Organisation checkForExperienceOrganisation(UUID organisationId) {
        return organisationRepository.findById(organisationId)
                .orElseThrow(() -> {
                   leLogger.warn("Organisation not found.");
                   throw new OrganisationNotFoundException(ApiError.builder()
                           .path(getCurrentRequest())
                           .error("Organisation not found")
                           .status(HttpStatus.NOT_FOUND)
                           .timestamp(LocalDateTime.now())
                           .build());
                });
    }

    private Education isTheEducationExistingOne(UUID educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    leLogger.warn("Education not found.");
                    throw new EducationNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Education not found.")
                            .status(HttpStatus.NOT_FOUND)
                            .timestamp(LocalDateTime.now())
                            .build());
                });
    }
}
