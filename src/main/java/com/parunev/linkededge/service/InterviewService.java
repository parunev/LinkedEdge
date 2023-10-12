package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.model.job.CompanyResolution;
import com.parunev.linkededge.model.job.Job;
import com.parunev.linkededge.model.payload.interview.*;
import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.service.extraction.ExtractionService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.parunev.linkededge.openai.OpenAiJobPrompt.SYSTEM_PREPARE_AND_COACH_FOR_INTERVIEW;
import static com.parunev.linkededge.openai.OpenAiJobPrompt.userPrepareAndCoachForInterview;
import static com.parunev.linkededge.openai.OpenAiPrompts.*;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

/**
 * The `InterviewService` class provides interview preparation services, question generation, coaching for job interviews,
 * and specialized interview question answering.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
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
    private final CompanyResolutionRepository companyResolutionRepository;
    private final CoachingRepository coachingRepository;
    private final PreparationRepository preparationRepository;
    private final InterviewPreparationRepository interviewPreparationRepository;
    private final UserProfileUtils upUtils;
    private final OpenAi openAi;
    private final ModelMapper modelMapper;
    private final ExtractionService extractionService;
    private final LELogger leLogger = new LELogger(InterviewService.class);

    /**
     * Prepares the user for a job interview by generating coaching and preparation content based on a provided job link.
     *
     * @param request The {@link JobRequest} object containing the job link.
     * @return A {@link JobResponse} with coaching and preparation content for the job interview.
     * @throws InvalidExtractException if job information cannot be extracted or is inappropriate/unrelated.
     * @throws ResourceNotFoundException if there are insufficient job credits.
     */
    public JobResponse prepareMeForAJob(JobRequest request){
        // Obtain the user and profile information using UserProfileUtils.
        Pair<User, Profile> pair = upUtils.getUserAndProfile();

        // Extract the job ID from the provided job link
        leLogger.info("Trying to find a job id if any");
        String jobId = extractJobIdIfAny(request.getJobLink());

        // Check for the availability of job credits.
        leLogger.info("Checking for available job credits");
        checkForCreditAvailability(pair.getRight().getJobCredits());

        // Extract job details based on the provided job link.
        leLogger.info("Job extraction");
        Job job = extractionService.createJob(pair.getRight(), jobId);

        // Retrieve company resolution information associated with the job.
        CompanyResolution companyResolution = companyResolutionRepository.findByJobId(job.getId())
                .orElseThrow(() -> {
                    leLogger.warn("No such company resolution found");
                    throw new InvalidExtractException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("No such Company Resolution find in the database")
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
                });

        // Generate AI model messages for job preparation and coaching.
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(SYSTEM_PREPARE_AND_COACH_FOR_INTERVIEW);
        messages.add(userPrepareAndCoachForInterview(companyResolution.getCompanyName(), companyResolution.getCompanyStaffCount()
        ,companyResolution.getSpecialties(), companyResolutionRepository.findAllByJobId(job.getId()), job.getJobDescription(),
                job.getEmploymentStatus(),job.getJobTitle(), job.getFunctions(), job.getIndustries(), pair.getRight().getDescription(),
                pair.getRight().getOrganisation(),pair.getRight().getEducation(), pair.getRight().getExperience(), pair.getRight().getSkill()));

        // Ask the AI model for job interview preparation content.
        String answer = openAi.ask(messages);

        // Build interview preparation content and coaching information.
        Pair<List<Coaching>, Preparation> interviewPreparation;
        try {
            interviewPreparation = buildInterviewPreparation(answer, job, pair.getRight());
        } catch (JSONException e){
            throw new InvalidExtractException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Either nothing was extract or the operation " +
                            "was aborted due to inappropriate or unrelated information.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        // Deduct job credits from the user's profile and update the profile.
        pair.getRight().setCredits(pair.getRight().getJobCredits() - 1);
        profileRepository.save(pair.getRight());

        // Build the response with coaching and preparation content.
        return JobResponse.builder()
                .coaching(interviewPreparation
                        .getLeft()
                        .stream()
                        .map(coaching -> modelMapper.map(coaching, CoachingResponse.class))
                        .toList())
                .preparationResponse(modelMapper.map(interviewPreparation.getRight(),PreparationResponse.class))
                .build();
    }

    /**
     * Builds interview preparation content and coaching based on the AI-generated answer.
     *
     * @param answer   The AI-generated answer in JSON format.
     * @param job      The job details associated with the interview.
     * @param profile  The user's profile for which the preparation is being generated.
     * @return A {@link Pair} containing a list of {@link Coaching} and a {@link Preparation} for interview preparation.
     * @throws JSONException if there are issues with parsing the AI-generated answer.
     */
    private Pair<List<Coaching>, Preparation> buildInterviewPreparation(String answer, Job job, Profile profile) throws JSONException {
        // Parse the AI-generated answer into a JSON object.
        JSONObject jsonObject = new JSONObject(answer);

        // Extract coaching and preparation content from the JSON object.
        JSONArray coachingArray = jsonObject.getJSONArray("coaching");
        JSONArray preparationArray = jsonObject.getJSONArray("preparation");

        // Create an InterviewPreparation entity to associate the content.
        InterviewPreparation interviewPreparation = InterviewPreparation.builder()
                .job(job)
                .profile(profile)
                .build();
        interviewPreparationRepository.save(interviewPreparation);

        // Initialize a list to store coaching content.
        List<Coaching> coachingList = new ArrayList<>();
        for (int i = 0; i < coachingArray.length() ; i++) {
            JSONObject coachingObject = coachingArray.getJSONObject(i);

            // Create Coaching entities for each coaching item.
            Coaching coaching = Coaching.builder()
                    .difficulty(QuestionDifficulty.valueOf(coachingObject.getString("difficulty")))
                    .question(coachingObject.getString("question"))
                    .insight(coachingObject.getString("insight"))
                    .answer(coachingObject.getString("advice"))
                    .interview(interviewPreparation)
                    .build();
            coachingRepository.save(coaching);
            coachingList.add(coaching);
        }

        // Create a Preparation entity for interview preparation.
        Preparation preparation = Preparation.builder()
                .doYouFit(preparationArray.getJSONObject(0).getString("doYouFit"))
                .doYouNotFit(preparationArray.getJSONObject(0).getString("doYouNotFit"))
                .interview(interviewPreparation)
                .build();
        preparationRepository.save(preparation);

        // Return a Pair containing coaching and preparation content.
        return Pair.of(coachingList, preparation);
    }

    /**
     * Generates an answer to a user's specialized interview question and stores it in the database.
     *
     * @param request The {@link AnswerRequest} containing the user's question.
     * @return An {@link AnswerResponse} containing the AI-generated answer, example, and benefits.
     * @throws UserProfileException if the AI-generated response is inappropriate or unrelated.
     * @throws InvalidExtractException if the AI response cannot be extracted or the operation is aborted.
     */
    public AnswerResponse answerUserQuestion(@Valid AnswerRequest request){
        // Get the user and profile information.
        Pair<User, Profile> pair = upUtils.getUserAndProfile();

        // Check if the user has sufficient credits for the operation.
        checkForCreditAvailability(pair.getRight().getCredits());

        // Deduct one credit from the user's balance.
        pair.getRight().setCredits(pair.getRight().getCredits() - 1);

        AnswerResponse response;
        try{
            // Generate an answer to the user's question using AI.
            response = generateAnswerForUserQuestion(request);
        } catch (JSONException e) {
            // Handle JSON parsing or extraction errors.
            throw new InvalidExtractException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Either nothing was extract or the operation " +
                            "was aborted due to inappropriate or unrelated question.")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        // Check if the response contains appropriate content.
        if (response.getExample().equals("") || response.getBenefits().equals("")){
            throw new UserProfileException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Operation was aborted due to inappropriate or unrelated question.")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        // Create a SpecializedAnswer entity to store the response in the database.
        SpecializedAnswer answer = SpecializedAnswer.builder()
                .profile(pair.getRight())
                .question(response.getQuestion())
                .answer(response.getAnswer())
                .example(response.getExample())
                .benefits(response.getBenefits())
                .build();

        // Save the updated profile and the AI-generated answer.
        profileRepository.save(pair.getRight());
        specializedAnswerRepository.save(answer);

        return response;
    }

    /**
     * Generates a list of random interview questions for the user based on their education, experience, skills, and desired difficulty.
     *
     * @param request The {@link QuestionRequest} containing the user's preferences for generating questions.
     * @return A list of {@link QuestionResponse} objects, representing the generated interview questions.
     * @throws InvalidWritingException if there is an issue with generating questions, such as missing information or errors.
     */
    public List<QuestionResponse> generateRandomInterviewQuestions(@Valid QuestionRequest request) {
        // Get the user and profile information.
        Pair<User, Profile> pair = upUtils.getUserAndProfile();

        // Check if the user has sufficient credits for the operation.
        checkForCreditAvailability(pair.getRight().getCredits());

        // Retrieve the user's chosen education, experience, skills, and difficulty.
        Education education = isTheEducationExistingOne(request.getEducation());
        Experience experience = isTheExperienceExistingOne(request.getExperience());
        List<Skill> skills = isTheSkillsExistingOnes(request.getSkills());
        QuestionDifficulty difficulty = request.getDifficulty();

        List<Question> questions;

        try{
            // Generate interview questions based on user preferences.
            questions = generateInterviewQuestions(pair.getRight(), education, experience, skills, difficulty);
        } catch (Exception e){
            // Handle exceptions related to question generation.
            leLogger.error(e.getMessage() + "Exception: {} Cause: {}",e, e.getCause());
            throw new InvalidWritingException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        // Deduct one credit from the user's balance.
        pair.getRight().setCredits(pair.getRight().getCredits() - 1);
        profileRepository.save(pair.getRight());

        // Convert the generated questions to QuestionResponse objects.
        leLogger.info("Questions generating operation successful, returning them to the user");
        return questions.stream()
                .map(question -> modelMapper.map(question, QuestionResponse.class))
                .toList();
    }

    /**
     * Generates an answer for a user's specialized interview question using OpenAI's natural language processing.
     *
     * @param request The {@link AnswerRequest} containing the user's question.
     * @return An {@link AnswerResponse} object containing the answer, example, and benefits for the user's question.
     * @throws JSONException if there is an issue with parsing the JSON response from OpenAI.
     */
    private AnswerResponse generateAnswerForUserQuestion(AnswerRequest request) throws JSONException {
        // Create a list of OpenAI messages to request an answer.
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(SYSTEM_ANSWER_SPECIALIZED_INTERVIEW_QUESTION_PROMPT);
        messages.add(userGenerateSpecializedAnswer(request.getQuestion()));

        // Request an answer from OpenAI.
        String answer = openAi.ask(messages);

        // Parse the JSON response from OpenAI.
        JSONObject jsonObject = new JSONObject(answer);

        // Build an AnswerResponse object with the question, answer, example, and benefits.
        return AnswerResponse.builder()
                .question(request.getQuestion())
                .answer(jsonObject.getString("answer"))
                .example(jsonObject.getString("example"))
                .benefits(jsonObject.getString("benefits"))
                .build();
    }

    /**
     * Generates a list of interview questions for a user based on their profile, education, experience, skills, and question difficulty.
     *
     * @param profile The user's profile for which questions are generated.
     * @param education The user's education details.
     * @param experience The user's work experience details.
     * @param skills The list of skills possessed by the user.
     * @param difficulty The desired difficulty level for the questions.
     * @return A list of {@link Question} objects representing the generated interview questions.
     * @throws JSONException if there is an issue with parsing the JSON response from OpenAI.
     */
    private List<Question> generateInterviewQuestions(Profile profile, Education education, Experience experience, List<Skill> skills, QuestionDifficulty difficulty) throws JSONException {
        leLogger.info("Starting the generation of interview questions");

        // Generate prompts based on the user's education and experience.
        String educationPrompt = generateEducatePrompt(education);
        String experiencePrompt = generateExperiencePrompt(experience);

        leLogger.info("Asking open ai to generate the interview questions");
        // Create a list of OpenAI messages to request interview questions.
        List<OpenAiMessage> messages = new ArrayList<>();

        messages.add(SYSTEM_INTERVIEW_QUESTION_PROMPT);
        messages.add(userInterviewQuestionsPrompt(
                educationPrompt,
                experiencePrompt,
                skills,
                difficulty
                ));

        // Request interview questions from OpenAI.
        String questions = openAi.ask(messages);
        leLogger.info("Questions are ready to be sent to the end-client");

        // Process and return the generated interview questions.
        return returnQuestionsAfterProcessing(profile, education, experience, skills, questions);
    }

    /**
     * Extracts the job ID from a given LinkedIn job link.
     *
     * @param jobLink The job link from which to extract the job ID.
     * @return The extracted job ID as a string.
     * @throws InvalidExtractException if the job ID cannot be extracted from the provided job link.
     */
    private String extractJobIdIfAny(String jobLink) {
        // Define regular expression patterns to match different job link formats.
        Pattern currentJobIdPattern = Pattern.compile("currentJobId=(\\d+)");
        Pattern sharedJobIdPattern = Pattern.compile("/view/(\\d+)");

        // Attempt to match the currentJobId pattern in the job link.
        Matcher matcher = currentJobIdPattern.matcher(jobLink);
        if (matcher.find()){
            // Log and return the extracted currentJobId.
            leLogger.info("Found currentJobId={}", matcher.group(1));
            return matcher.group(1);
        } else {
            // If currentJobId pattern doesn't match, try the sharedJobId pattern.
            matcher = sharedJobIdPattern.matcher(jobLink);
            if (matcher.find()){
                // Log and return the extracted job ID from the sharedJobId pattern.
                leLogger.info("JobId presented in the link:{}", matcher.group(1));
                return matcher.group(1);
            } else {
                // Log a warning and throw an exception if no job ID is found in the provided job link.
                leLogger.warn("JobId not found in the provided job link: {}", jobLink);
                throw new InvalidExtractException(ApiError.builder()
                        .path(getCurrentRequest())
                        .error("Job ID not found in the provided job link: " + jobLink)
                        .status(HttpStatus.NOT_FOUND)
                        .timestamp(LocalDateTime.now())
                        .build());
            }
        }
    }

    /**
     * Processes and saves the extracted interview questions to the database.
     *
     * @param profile The user's profile for which the questions are generated.
     * @param education The user's education information for context.
     * @param experience The user's experience information for context.
     * @param skill The list of skills to match with questions.
     * @param questions The JSON string containing the extracted interview questions.
     * @return A list of saved interview questions.
     * @throws JSONException if there are issues with parsing the JSON string.
     * @throws InvalidExtractException if no questions are extracted from the response.
     */
    private List<Question> returnQuestionsAfterProcessing(Profile profile, Education education,
                                                          Experience experience, List<Skill> skill, String questions) throws JSONException {
        leLogger.info("Starting the questions processing");

        // Parse the JSON string to extract interview questions.
        JSONObject object = new JSONObject(questions);
        JSONArray arrayOfQuestions = object.getJSONArray("questions");

        leLogger.info("Validating the array of questions");
        if (arrayOfQuestions.length() == 0){
            // If no questions are extracted, log a warning and throw an exception.
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

            // Create and save a Question object with extracted data.
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

    /**
     * Generates a prompt based on the user's experience information.
     *
     * @param experience The user's experience information to create the prompt.
     * @return A formatted string prompt based on the user's experience.
     */
    private String generateExperiencePrompt(Experience experience) {
        StringBuilder message = new StringBuilder();

        // Fetch the organization details associated with the experience.
        Organisation organisation = checkForExperienceOrganisation(experience.getOrganisation().getId());

        if (!experience.getTitle().isEmpty()){
            // Append the job title to the message.
            message.append(experience.getTitle()).append(", ");
            leLogger.debug("Appended Job Title {}", message.toString());
        }

        if (!organisation.getName().isEmpty()){
            // Append the organization name to the message.
            message.append(organisation.getName()).append(", ");
            leLogger.debug("Appended Organisation Name {}", message.toString());
        }

        if (experience.getDateStarted() != null && !experience.getDateStarted().isEmpty()){
            // Append the date started to the message.
            message.append(experience.getDateStarted()).append("-");
            leLogger.debug("Appended Experience Date Started {}", message.toString());
        }

        if (experience.getDateEnded() != null && !experience.getDateEnded().isEmpty()){
            // Append the date ended to the message.
            message.append(experience.getDateEnded());
            leLogger.debug("Appended Experience Date Ended {}", message.toString());
        }

        leLogger.info("Final Experience Prompt: {}", message.toString());
        return message.toString();
    }

    /**
     * Generates a prompt based on the user's education information.
     *
     * @param education The user's education information to create the prompt.
     * @return A formatted string prompt based on the user's education.
     */
    private String generateEducatePrompt(Education education) {
        StringBuilder message = new StringBuilder();

        if (!education.getInstitutionName().isEmpty()){
            // Append the institution name to the message.
            message.append(education.getInstitutionName()).append(", ");
            leLogger.debug("Appended Institution Name: {}", message.toString());
        }

        if (!education.getFieldOfStudy().equals("DATA NOT PRESENT")){
            // Append the field of study to the message.
            message.append(education.getFieldOfStudy()).append(",");
            leLogger.debug("Appended Field of Study: {}", message.toString());
        }

        if (!education.getDegree().equals("DATA NOT PRESENT")){
            // Append the degree to the message.
            message.append(education.getDegree()).append(", ");
            leLogger.debug("Appended Degree: {}", message.toString());
        }

        if (!education.getDateStarted().equals("NOT PRESENT")){
            // Append the date started to the message.
            message.append(education.getDateStarted()).append("-");
            leLogger.debug("Appended Education Date Started: {}", message.toString());
        }

        if (!education.getDateEnded().equals("NOT PRESENT")){
            // Append the date ended to the message.
            message.append(education.getDateEnded());
            leLogger.debug("Appended Education Date Ended: {}", message.toString());
        }

        leLogger.info("Final Education Prompt: {}", message.toString());
        return message.toString();
    }

    /**
     * Checks if the user has sufficient credits for an operation.
     *
     * @param credits The number of credits available for the user.
     * @throws ResourceNotFoundException If the user has insufficient credits, this method
     *                                  throws a ResourceNotFoundException with an error message.
     */
    private void checkForCreditAvailability(Integer credits) {
        if (credits == 0){
            leLogger.warn("Insufficient credits.");

            // Throw a ResourceNotFoundException with an error message indicating
            // that the user should consider buying more credits for their profile.
            throw new ResourceNotFoundException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Insufficient credits. Consider buying more credits for your profile!")
                    .status(HttpStatus.BAD_REQUEST)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    /**
     * Verifies the existence of multiple skills in the skill repository.
     *
     * @param skills A list of UUIDs representing the skills to be verified.
     * @return A list of Skill objects corresponding to the provided UUIDs.
     * @throws ResourceNotFoundException If one or more of the provided skills do not exist in
     *                                  the skill repository, this method throws a ResourceNotFoundException
     *                                  with an error message.
     */
    private List<Skill> isTheSkillsExistingOnes(List<UUID> skills) {
        // Retrieve a list of skills from the skill repository based on the provided UUIDs.
        if (skills.size() != skillRepository.findAllById(skills).size()){
            leLogger.warn("One or more skills not found.");

            // Throw a ResourceNotFoundException with an error message specifying that one or
            // more of the provided skills were not found in the repository.
            throw new ResourceNotFoundException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("One or more skills not found.")
                    .status(HttpStatus.NOT_FOUND)
                    .timestamp(LocalDateTime.now())
                    .build());
        } else {
            // Return the list of Skill objects corresponding to the provided UUIDs.
            return skillRepository.findAllById(skills);
        }
    }

    /**
     * Retrieves an Experience entity by its UUID from the experience repository.
     *
     * @param experienceId The UUID of the Experience entity to retrieve.
     * @return The Experience entity if found.
     * @throws ResourceNotFoundException If the Experience entity with the provided UUID is not found in
     *                                  the repository, this method throws a ResourceNotFoundException with
     *                                  an error message.
     */
    private Experience isTheExperienceExistingOne(UUID experienceId) {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() ->{
                    leLogger.warn("Experience not found.");

                    // Throw a ResourceNotFoundException with an error message specifying that the
                    // Experience entity was not found in the repository.
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Experience not found.")
                            .status(HttpStatus.NOT_FOUND)
                            .timestamp(LocalDateTime.now())
                            .build());
                });
    }

    /**
     * Retrieves an Organisation entity by its UUID from the organisation repository.
     *
     * @param organisationId The UUID of the Organisation entity to retrieve.
     * @return The Organisation entity if found.
     * @throws ResourceNotFoundException If the Organisation entity with the provided UUID is not found in
     *                                  the repository, this method throws a ResourceNotFoundException with
     *                                  an error message.
     */
    private Organisation checkForExperienceOrganisation(UUID organisationId) {
        return organisationRepository.findById(organisationId)
                .orElseThrow(() -> {
                   leLogger.warn("Organisation not found.");

                    // Throw a ResourceNotFoundException with an error message specifying that the
                    // Organisation entity was not found in the repository.
                   throw new ResourceNotFoundException(ApiError.builder()
                           .path(getCurrentRequest())
                           .error("Organisation not found")
                           .status(HttpStatus.NOT_FOUND)
                           .timestamp(LocalDateTime.now())
                           .build());
                });
    }

    /**
     * Retrieves an Education entity by its UUID from the education repository.
     *
     * @param educationId The UUID of the Education entity to retrieve.
     * @return The Education entity if found.
     * @throws ResourceNotFoundException If the Education entity with the provided UUID is not found in
     *                                  the repository, this method throws a ResourceNotFoundException with
     *                                  an error message.
     */
    private Education isTheEducationExistingOne(UUID educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> {
                    leLogger.warn("Education not found.");

                    // Throw a ResourceNotFoundException with an error message specifying that the
                    // Education entity was not found in the repository.
                    throw new ResourceNotFoundException(ApiError.builder()
                            .path(getCurrentRequest())
                            .error("Education not found.")
                            .status(HttpStatus.NOT_FOUND)
                            .timestamp(LocalDateTime.now())
                            .build());
                });
    }
}
