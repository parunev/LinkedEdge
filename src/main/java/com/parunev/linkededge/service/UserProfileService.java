package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.model.enums.TokenType;
import com.parunev.linkededge.model.enums.ValidValue;
import com.parunev.linkededge.model.payload.interview.AnswerResponse;
import com.parunev.linkededge.model.payload.interview.QuestionResponse;
import com.parunev.linkededge.model.payload.profile.ProfileMfaRequest;
import com.parunev.linkededge.model.payload.profile.ProfileResponse;
import com.parunev.linkededge.model.payload.profile.education.EducationResponse;
import com.parunev.linkededge.model.payload.profile.education.ProfileEducationRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailResponse;
import com.parunev.linkededge.model.payload.profile.experience.ExperienceResponse;
import com.parunev.linkededge.model.payload.profile.experience.ProfileExperienceRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordResponse;
import com.parunev.linkededge.model.payload.profile.skill.ProfileSkillRequest;
import com.parunev.linkededge.model.payload.profile.skill.SkillResponse;
import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.ResourceNotFoundException;
import com.parunev.linkededge.security.exceptions.UserProfileException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.UserProfileUtils;
import com.parunev.linkededge.util.email.EmailSender;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.parunev.linkededge.model.enums.ValidValue.*;
import static com.parunev.linkededge.openai.OpenAiPrompts.*;
import static com.parunev.linkededge.util.ConfirmationTokenUtil.isValidToken;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
import static com.parunev.linkededge.util.email.EmailPatterns.changeEmailAddress;
import static com.parunev.linkededge.util.email.EmailPatterns.changeUserPasswordEmail;

/**
 * The `UserProfileService` class provides various services related to user profiles.
 * It allows users to manage their profile information, including changing passwords,
 * email addresses, adding education, experience, and skills, enabling or disabling
 * two-factor authentication (2FA), and more.
 * <p>
 * This class is part of the LinkedEdge application and is responsible for handling
 * user profile-related operations and interactions.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */

@Service
@Validated
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final OrganisationRepository organisationRepository;
    private final QuestionRepository questionRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final OpenAi openAi;
    private final UserProfileUtils upUtils;
    private final EmailSender emailSender;
    private final LELogger leLogger = new LELogger(UserProfileService.class);

    /**
     * Allows a user to change their password securely.
     *
     * @param request The user's request to change the password.
     * @return A `ProfileChangePasswordResponse` indicating the result of the password change operation.
     * @throws UserProfileException If any validation checks fail during the password change process.
     */
    public ProfileChangePasswordResponse changeUserPassword(@Valid ProfileChangePasswordRequest request){
        // Retrieve the user making the request
        User user = upUtils.findUserByContextHolder();

        // Check if the provided old password matches the user's current password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            leLogger.warn("The provided old password does not match the user one");
            throw new UserProfileException(buildError("The provided old password does not match your current one", HttpStatus.BAD_REQUEST));
        }

        // Check if the new password and confirmation match
        if (!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())){
            leLogger.warn("Additional validation entered. New passwords does not match");
            throw new UserProfileException(buildError("The provided passwords does not match", HttpStatus.BAD_REQUEST));
        }

        // Password validations passed, proceed to change the user's password
        leLogger.info("Password validations passed. User password will be changed");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Send an email notification about the password change
        emailSender.send(user.getEmail(), changeUserPasswordEmail(user.getFullName()), "LinkedEdge: Change Password (Do not ignore)");

        // Return a response indicating a successful password change
        return ProfileChangePasswordResponse.builder()
                .path(getCurrentRequest())
                .message("Your password has been changed! We've sent you an email!")
                .email(user.getEmail())
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Allows a user to request a change of their email address.
     *
     * @param request The user's request to change their email address.
     * @return A `ProfileEmailResponse` indicating the result of the email change request.
     * @throws UserProfileException If any validation checks fail during the email change process.
     */
    public ProfileEmailResponse changeUserEmail(@Valid ProfileEmailRequest request){
        // Retrieve the user making the request
        User user = upUtils.findUserByContextHolder();

        // Check if the provided new email is the same as the current one or if it exists in the database
        if (user.getEmail().equals(request.getNewEmail())){
            leLogger.warn("The email is the same as the current one used by the user");
            throw new UserProfileException(buildError("The provided email is the same as your current one!", HttpStatus.BAD_REQUEST));
        } else if (userRepository.existsByEmail(request.getNewEmail())){
            leLogger.warn("The email is already associated with a different user profile");
            throw new UserProfileException(buildError("The provided email is already associated with another user profile", HttpStatus.BAD_REQUEST));
        }

        // Check if the provided user password matches the user's current password
        if (!passwordEncoder.matches(request.getUserPassword(), user.getPassword())){
            leLogger.warn("The provided password does not match the user one");
            throw new UserProfileException(buildError("The provided password does not match your password.", HttpStatus.BAD_REQUEST));
        }

        // Remove current authorization for the user and revoke related tokens
        leLogger.debug("Current authorization for {} & any related to him token will be removed", user.getUsername());
        deleteUserJwtTokens(user);

        // Generate a confirmation token for the email change
        ConfirmationToken token = ConfirmationToken.builder()
                .user(user)
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusMinutes(15))
                .build();
        confirmationTokenRepository.save(token);

        // Encode the new email for security
        String encodedEmail = enDecEmail(request.getNewEmail(), "encode");

        // Send an email with a confirmation link for the new email address
        emailSender.send(request.getNewEmail(), changeEmailAddress(user.getFullName()
         , "http://localhost:8080/edge-api/v1/profile/change-email/confirm?token=" + token.getTokenValue() + "&e=" + encodedEmail),
                "LinkedEdge: Request to change your email address");

        // Return a response indicating a successful email change request
        return ProfileEmailResponse.builder()
                .path(getCurrentRequest())
                .message("Email change request sent. Please confirm your new email address!")
                .newEmail(request.getNewEmail())
                .isEnabled(user.isEnabled())
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Encodes or decodes an email address using Base64 encoding.
     *
     * @param email The email address to be encoded or decoded.
     * @param operation The operation to be performed, either "encode" or "decode".
     * @return The result of the encoding or decoding operation.
     */
    private String enDecEmail(String email, String operation) {
        byte[] bytes;
        if(operation.equals("encode")){
            // If the operation is to encode, convert the email address to bytes using UTF-8 encoding
            bytes = email.getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(bytes);
        } else {
            // If the operation is to decode, decode the Base64-encoded email address
            bytes = Base64.getDecoder().decode(email);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Verifies and updates the user's email address after confirming the email change request.
     *
     * @param token The confirmation token generated for the email change request.
     * @param email The encoded email address to be decoded and set as the new email.
     * @return A response indicating the success of the email change operation.
     * @throws UserProfileException If the token is not found, expired, or already confirmed.
     */
    @Transactional
    public ProfileEmailResponse verifyChangeUserEmail(String token, String email){
        // Retrieve the confirmation token from the database using its token value
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByTokenValue(token)
                .orElseThrow(() -> {
                    leLogger.warn("Token not found");
                    throw new UserProfileException(
                            buildError("Token not found. Please ensure you have the correct token or request a new one.", HttpStatus.NOT_FOUND)
                    );
                });

        // Check if the confirmation token is valid (not expired or already confirmed)
        isValidToken(confirmationToken);

        // Retrieve the user associated with the confirmation token
        User user = confirmationToken.getUser();

        // Update the confirmation token's confirmed timestamp to mark it as confirmed
        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

        // Decode the encoded email address using the enDecEmail method
        String decodedEmail = enDecEmail(email, "decode");

        // Set the decoded email address as the new email for the user
        user.setEmail(decodedEmail);

        // Save the updated user entity with the new email address
        userRepository.save(user);

        // Log the successful email change and return a response indicating success
        leLogger.info("The email has been changed successfully!");
        return ProfileEmailResponse.builder()
                .path(getCurrentRequest())
                .message("Your email was been changed successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Updates the Two-Factor Authentication (2FA) status for the user.
     *
     * @param request The request containing the updateMfa flag.
     * @return A response indicating the status of the 2FA update operation.
     */
    public ProfileResponse updateUserMfa(@Valid ProfileMfaRequest request){
        // Retrieve the user based on the current security context
        User user = upUtils.findUserByContextHolder();

        // Log whether 2FA authentication is enabled or disabled for the user
        if (request.isUpdateMfa()) {
            leLogger.info("2FA Authentication enabled for user: {}", user.getUsername());
        } else {
            leLogger.info("2FA Authentication disabled for user: {}", user.getUsername());
        }

        // Update the 2FA status for the user
        updateMfa(user, request.isUpdateMfa());

        // Construct a message based on the 2FA status change
        String message = user.isMfaEnabled()
                ? "Your 2FA Authentication has been enabled"
                : "Your 2FA has been disabled";

        // Build and return a response containing the request path, message, timestamp, and HTTP status
        return ProfileResponse.builder()
                .path(getCurrentRequest())
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Adds a new education entry to the user's profile.
     *
     * @param request The request containing the details of the education to be added.
     * @return A response indicating the success of the education addition.
     */
    public ProfileResponse addEducation(@Valid ProfileEducationRequest request){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("Education creation started for user: {}", pair.getLeft().getUsername());

        // Check the validity of the provided education details using AI validation
        checkIfValid(VALID_EDUCATION, request.getInstitutionName() + ", " +
               request.getFieldOfStudy() + ", " + request.getDegree());

        // Check if there is sufficient capacity to add additional education entries
        checkForCreditsCapacity(pair.getRight().getEducationExtraCapacity());

        // Create an Education object with the provided details
        Education education = Education.builder()
               .profile(pair.getRight())
               .institutionName(request.getInstitutionName())
               .degree(request.getDegree())
               .fieldOfStudy(request.getFieldOfStudy())
               .dateStarted(request.getDateStarted() != null ? request.getDateStarted() : "NOT PRESENT")
               .dateEnded(request.getDateEnded() != null ? request.getDateEnded() : "NOT PRESENT")
               .build();
       educationRepository.save(education);
       leLogger.info("Education saved successfully for user: {}", pair.getLeft().getUsername());

        // Decrement the education extra capacity of the user's profile
       pair.getRight().setEducationExtraCapacity(pair.getRight().getEducationExtraCapacity() - 1);
       profileRepository.save(pair.getRight());

       leLogger.info("Education saved to user profile successfully for user: {}", pair.getLeft().getUsername());

       // Construct a response message indicating the successful addition of education
       // Build and return a response containing the request path, message, timestamp, and HTTP status
       return buildResponse(("""
               You've successfully added a new education to your profile
               Institution name: %s
                Degree: %s
                Field of study: %s
               """)
               .formatted(education.getInstitutionName(),education.getDegree(),education.getFieldOfStudy()));
    }

    /**
     * Retrieves and returns all education records associated with the user's profile.
     *
     * @return A response containing a list of education records for the user.
     * @throws UserProfileException if no education records are found for the user.
     */
    public ProfileResponse returnAllUserEducations(){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Retrieve the list of education records associated with the user's profile
        List<Education> educationsList = pair
                .getRight()
                .getEducation();

        // Check if the list of education records is empty
        if (educationsList.isEmpty()){
            // If no education records are found, log a warning and throw an exception
            leLogger.warn("No education records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no education records.", HttpStatus.NOT_FOUND));
        }

        // Map the list of Education objects to a list of EducationResponse objects
        List<EducationResponse> educations = map(educationsList, EducationResponse.class);

        leLogger.info("Retrieved all education records for user: {}", pair.getLeft().getUsername());
        // Build a response containing the list of education records
        return ProfileResponse.builder()
                .educations(educations)
                .build();
    }

    /**
     * Adds a new work experience to the user's profile.
     *
     * @param request The request object containing information about the experience to be added.
     * @return A response indicating the successful addition of the experience.
     * @throws UserProfileException if the operation fails or user exceeds their allowed capacity for experiences.
     */
    public ProfileResponse addExperience(@Valid ProfileExperienceRequest request){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("Experience creation started for user: {}", pair.getLeft().getUsername());

        // Check if the provided experience information is valid
        checkIfValid(VALID_EXPERIENCE, "Job Title: " + request.getTitle() + ", " + request.getDescription());

        // Check if the user exceeds their allowed capacity for experiences
        checkForCreditsCapacity(pair.getRight().getExperienceExtraCapacity());
        leLogger.info("Experience creation started for user {}", pair.getLeft().getUsername());

        // Create an Organisation object associated with the user's profile
        Organisation organisation = Organisation.builder()
                .profile(pair.getRight())
                .name(request.getOrganisationName())
                .salesNavLink("MANUALLY ADDED")
                .build();
        organisationRepository.save(organisation);
        leLogger.info("Organisation '{}' saved successfully.", organisation.getName());

        // Create an Experience object associated with the user's profile and the organisation
        Experience experience = Experience.builder()
                .profile(pair.getRight())
                .organisation(organisation)
                .description(request.getDescription())
                .title(request.getTitle())
                .dateStarted(!request.getDateStarted().isEmpty() ? request.getDateStarted() : "NOT PRESENT")
                .dateEnded(!request.getDateEnded().isEmpty() ? request.getDateEnded() : "NOT PRESENT")
                .location(request.getLocation())
                .build();
        experienceRepository.save(experience);
        leLogger.info("Experience saved successfully.");

        // Reduce the user's experience extra capacity by 1 and save the profile
        pair.getRight().setExperienceExtraCapacity(pair.getRight().getExperienceExtraCapacity() - 1);
        profileRepository.save(pair.getRight());

        leLogger.info("Experience added to the profile of user: {}", pair.getLeft().getUsername());

        // Construct and return a response indicating the successful addition of the experience
        return buildResponse("You've successfully added a new experience to your profile%n%nOrganisation: %s%nDescription: %s%nTitle: %s%nLocation:%s%n"
                .formatted(organisation.getName(), experience.getDescription(), experience.getTitle(), experience.getLocation()));
    }

    /**
     * Retrieves all work experiences associated with the user's profile.
     *
     * @return A response containing a list of the user's work experiences.
     * @throws UserProfileException if no experience records are found for the user.
     */
    public ProfileResponse returnAllUserExperiences(){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Get the list of work experiences associated with the user's profile
        List<Experience> experiencesList = pair
                .getRight()
                .getExperience();

        // If no experience records are found, log a warning and throw an exception
        if (experiencesList.isEmpty()){
            leLogger.warn("No experience records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no experience records.", HttpStatus.NOT_FOUND));
        }

        // Map the list of experiences to ExperienceResponse objects
        List<ExperienceResponse> experiences = map(experiencesList, ExperienceResponse.class);

        leLogger.info("Retrieved all experiences for user: {}", pair.getLeft().getUsername());

        // Construct and return a response containing the list of experiences
        return ProfileResponse.builder()
                .experiences(experiences)
                .build();
    }

    /**
     * Adds a new skill to the user's profile.
     *
     * @param request The request containing the skill information to be added.
     * @return A response indicating the successful addition of the skill to the user's profile.
     * @throws UserProfileException if the skill is invalid, the user has exceeded their skill capacity, or if the skill already exists.
     */
    public ProfileResponse addSkill(@Valid ProfileSkillRequest request){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("ProfileSkillRequest received for user: {}", pair.getLeft().getUsername());

        leLogger.info("Checking if the skill '{}' is valid.", request.getName());

        // Check if the skill name is valid
        checkForCreditsCapacity(pair.getRight().getSkillExtraCapacity());
        checkIfValid(VALID_SKILL, request.getName());

        // Check if the skill already exists for the user
        checkForExistingSkill(pair.getRight(), request);

        leLogger.info("Skill creation started for user {}", pair.getLeft().getUsername());

        // Create a new Skill object with the provided information and save it to the database
        Skill skill = Skill.builder()
                .profile(pair.getRight())
                .name(request.getName())
                .numOfEndorsement(Integer.parseInt(request.getNumOfEndorsement()))
                .build();
        skillRepository.save(skill);
        leLogger.info("Skill saved successfully");

        // Decrement the skill capacity of the user's profile and save the profile
        pair.getRight().setSkillExtraCapacity(pair.getRight().getSkillExtraCapacity() - 1);
        profileRepository.save(pair.getRight());

        leLogger.info("Skill added to the profile of user: {}", pair.getLeft().getUsername());

        // Construct and return a response indicating the successful addition of the skill
        return buildResponse("You've successfully added a new skill to your profile%n%nSkill name: %s%nSkill level: %d%n"
                .formatted(skill.getName(), skill.getNumOfEndorsement()));
    }

    /**
     * Retrieves all skills associated with the user's profile.
     *
     * @return A response containing a list of skills associated with the user's profile.
     * @throws UserProfileException if the user has no skill records.
     */
    public ProfileResponse returnAllUserSkills(){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Get the list of skills associated with the user's profile
        List<Skill> skillList = pair
                .getRight()
                .getSkill();

        // Check if the user has any skill records; if not, raise an exception
        if (skillList.isEmpty()){
            leLogger.warn("No skill records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no skill records.", HttpStatus.NOT_FOUND));
        }

        // Map the skill objects to SkillResponse objects
        List<SkillResponse> skills = map(skillList, SkillResponse.class);

        leLogger.info("Retrieved all skills for user: {}", pair.getLeft().getUsername());

        // Construct and return a response containing the list of skills
        return ProfileResponse.builder()
                .skills(skills)
                .build();
    }

    /**
     * Retrieves a question by its unique identifier.
     *
     * @param questionId The unique identifier (UUID) of the question to retrieve.
     * @return A response containing the question details if found.
     * @throws ResourceNotFoundException if the question with the specified ID is not found or doesn't belong to the user's profile.
     */
    public QuestionResponse returnQuestionById(UUID questionId){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Attempt to find the question by its unique identifier
        Optional<Question> question = questionRepository.findById(questionId);

        if (question.isPresent()){
            // A question with the specified ID was found
            Question questionToReturn = question.get();

            // Check if the found question belongs to the specified user and profile
            if (questionToReturn.getProfile().equals(pair.getRight())){
                leLogger.info("Question with ID:{} found and belongs to the specified user and profile.", questionId);

                // Map the question object to a QuestionResponse object and return it
                return modelMapper.map(question, QuestionResponse.class);
            } else {
                // The found question doesn't belong to the user's profile; raise an exception
                leLogger.warn("Question with ID:{} does not belong to the specified user and profile.", questionId);
                throw new ResourceNotFoundException(buildError("This questions does not belong to your profile", HttpStatus.BAD_REQUEST));
            }
        } else {
            // The question with the specified ID was not found; raise an exception
            leLogger.warn("Question with ID:{} not found.", questionId);
            throw new ResourceNotFoundException(buildError("Question not present in the database", HttpStatus.NOT_FOUND));
        }
    }

    /**
     * Search for questions based on specified criteria, such as skill, difficulty, experience, and education.
     *
     * @param skill         The skill keyword to search for within questions.
     * @param difficulty    The desired difficulty level of the questions.
     * @param experienceId  The unique identifier (UUID) of the experience associated with the questions.
     * @param educationId   The unique identifier (UUID) of the education associated with the questions.
     * @param pageable      The paging and sorting information for the result page.
     * @return A paginated list of questions matching the specified criteria.
     */
    public Page<QuestionResponse> searchQuestions(String skill, QuestionDifficulty difficulty, UUID experienceId, UUID educationId, Pageable pageable){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Query the database for questions based on the provided search criteria
        Page<Question> questionPage = questionRepository.findAllQuestions(
                skill,
                difficulty,
                experienceId,
                educationId,
                pair.getRight().getId(),
                pageable
        );

        // Map the result Question objects to QuestionResponse objects
        return new PageImpl<>(questionPage
                .stream()
                .map(this::map)
                .toList(), pageable, questionPage.getSize());
    }

    /**
     * Search for answers based on specified criteria, such as date range and input text.
     *
     * @param fromDate   The start date of the date range for answer creation.
     * @param toDate     The end date of the date range for answer creation.
     * @param input      The input text to search for within answers.
     * @param pageable   The paging and sorting information for the result page.
     * @return A paginated list of answers matching the specified criteria.
     */
    public Page<AnswerResponse> searchAnswers(LocalDate fromDate, LocalDate toDate, String input, Pageable pageable){
        // Retrieve the user and profile pair using the utility method
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        // Query the database for answers based on the provided search criteria
        Page<SpecializedAnswer> answersPage = questionRepository.findAllAnswers(
                fromDate,
                toDate,
                input,
                pair.getRight().getId(),
                pageable
        );

        // Map the result SpecializedAnswer objects to AnswerResponse objects
        return new PageImpl<>(answersPage
                .stream()
                .map(answer -> modelMapper.map(answer, AnswerResponse.class))
                .toList(), pageable, answersPage.getSize());
    }

    /**
     * Maps a Question entity to a QuestionResponse DTO for response purposes.
     *
     * @param question The Question entity to be mapped to a QuestionResponse.
     * @return A QuestionResponse DTO representing the mapped Question entity.
     */
    private QuestionResponse map(Question question){
        return QuestionResponse.builder()
                .questionId(question.getId())
                .skillValue(question.getSkillValue())
                .questionValue(question.getQuestionValue())
                .exampleAnswer(question.getExampleAnswer())
                .difficulty(question.getDifficulty())
                .build();
    }

    /**
     * Checks for the existence of a skill with the same name in the user's profile.
     * If a skill with the same name already exists, a UserProfileException is thrown.
     *
     * @param profile The user's profile in which to check for the existing skill.
     * @param request The ProfileSkillRequest containing the name of the skill to check.
     * @throws UserProfileException If a skill with the same name already exists in the user's profile.
     */
    private void checkForExistingSkill(Profile profile, ProfileSkillRequest request) {
        leLogger.info("Checking for an existing skill in the user profile");

        // Search for a Skill entity with the same name as the skill specified in the ProfileSkillRequest.
        Skill skill = skillRepository.findByName(request.getName());

        // If a skill with the same name is found, check if it is already associated with the user's profile.
        if (skill != null){
            boolean isPresent = profile.getSkill().contains(skill);
            if (isPresent){
                // If the skill is already present in the user's profile, throw a UserProfileException with an error message.
                throw new UserProfileException(buildError("The skill you're trying to enter is already in your account",HttpStatus.BAD_REQUEST));
            }
        }
    }

    /**
     * Maps a list of source objects to a list of destination objects using ModelMapper.
     *
     * @param sourceList        The list of source objects to be mapped.
     * @param destinationClass  The class type of the destination objects.
     * @param <T>               The type of source objects.
     * @param <R>               The type of destination objects.
     * @return A list of destination objects resulting from the mapping.
     */
    private <T, R> List<R> map(List<T> sourceList, Class<R> destinationClass) {
        return sourceList.stream()
                .map(item -> modelMapper.map(item, destinationClass))
                .toList();
    }

    /**
     * Update the Multi-Factor Authentication (MFA) status for a user.
     *
     * @param user    The user for whom MFA status will be updated.
     * @param option  A boolean flag indicating whether to enable or disable MFA.
     */
    private void updateMfa(User user, boolean option) {
        // Check the current MFA status and the desired option.
        // If MFA is disabled and the option is to enable it, set MFA as enabled.
        // If MFA is enabled and the option is to disable it, set MFA as disabled.
        if (!user.isMfaEnabled() && option){
            leLogger.info("Updating MFA enabled for user: {}", user.getUsername());
            user.setMfaEnabled(true);
        } else if (user.isMfaEnabled() && !option){
            leLogger.info("Updating MFA disabled for user: {}", user.getUsername());
            user.setMfaEnabled(false);
        }
        userRepository.save(user);
    }

    private ProfileResponse buildResponse(String message){
        return ProfileResponse.builder()
                .path(getCurrentRequest())
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Check if there is sufficient extra capacity for adding experiences or education to a user's profile.
     *
     * @param capacity The remaining extra capacity for experiences or education.
     *
     * @throws ResourceNotFoundException When the capacity is zero, indicating insufficient extra capacity.
     */
    private void checkForCreditsCapacity(Integer capacity) {
        // Check if the provided capacity is zero, which indicates insufficient extra capacity.
        if (capacity == 0){
            throw new ResourceNotFoundException(buildError(
                    "Sorry, not enough extra capacity for experiences. Consider buying more credits for your profile!",
                    HttpStatus.BAD_REQUEST
            ));
        }
    }

    /**
     * Check if a provided value is valid using OpenAI's GPT-3.5 Turbo model and predefined validation prompts.
     *
     * @param value    The type of value to validate, e.g., skill, education, or experience.
     * @param toCheck  The value to be validated.
     *
     * @throws UserProfileException When the provided value is determined to be invalid.
     */
    private void checkIfValid(ValidValue value, String toCheck) {
        List<OpenAiMessage> messages = new ArrayList<>();
        String errorMessage = null;

        switch (value){
            case VALID_SKILL -> {
                messages.add(SYSTEM_IS_IT_VALID_SKILL);
                messages.add(userIsItValidSkill(toCheck));
                errorMessage = "The provided skill is not valid.";
            }
            case VALID_EDUCATION -> {
                messages.add(SYSTEM_IS_IT_VALID_EDUCATION);
                messages.add(userIsItValidEducation(toCheck));
                errorMessage = "The provided education is not valid.";
            }
            case VALID_EXPERIENCE -> {
                messages.add(SYSTEM_IS_IT_VALID_EXPERIENCE);
                messages.add(userIsItValidExperience(toCheck));
                errorMessage = "The provided experience is not valid.";
            }
        }

        // Use OpenAI's GPT-3.5 Turbo model to ask validation questions and retrieve the answer.
        String answer = openAi.ask(messages);

        // If the answer from the model is "no," it indicates validation failure.
        if (answer.equals("no")) {
            leLogger.warn("Validation failed: {}", errorMessage);
            throw new UserProfileException(buildError(errorMessage, HttpStatus.BAD_REQUEST));
        } else {
            leLogger.info("Validation passed for: {}", toCheck);
        }
    }

    /**
     * Delete and revoke all JWT tokens associated with a user.
     *
     * @param user The user for whom JWT tokens need to be revoked and deleted.
     */
    private void deleteUserJwtTokens(User user) {
        leLogger.warn("Revoking and deleting any tokens related to {}", user.getUsername());

        // Retrieve all valid JWT tokens associated with the user.
        List<JwtToken> tokens = jwtTokenRepository.findAllValidTokenByUserId(user.getId());

        if (tokens.isEmpty()){
            leLogger.info("No tokens found to delete");
            return;
        }

        // Delete all tokens found, effectively revoking them.
        jwtTokenRepository.deleteAll(tokens);
    }

    private ApiError buildError(String message, HttpStatus status){
        return ApiError.builder()
                .path(getCurrentRequest())
                .error(message)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }
}
