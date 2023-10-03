package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.model.enums.TokenType;
import com.parunev.linkededge.model.enums.ValidValue;
import com.parunev.linkededge.model.payload.interview.QuestionResponse;
import com.parunev.linkededge.model.payload.profile.ProfileMfaRequest;
import com.parunev.linkededge.model.payload.profile.ProfileResponse;
import com.parunev.linkededge.model.payload.profile.education.EducationResponse;
import com.parunev.linkededge.model.payload.profile.education.ProfileEducationRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailResponse;
import com.parunev.linkededge.model.payload.profile.experience.ExperienceResponse;
import com.parunev.linkededge.model.payload.profile.experience.ProfileExperienceRequest;
import com.parunev.linkededge.model.payload.profile.skill.ProfileSkillRequest;
import com.parunev.linkededge.model.payload.profile.skill.SkillResponse;
import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.InsufficientCapacityException;
import com.parunev.linkededge.security.exceptions.QuestionNotFoundException;
import com.parunev.linkededge.security.exceptions.RegistrationFailedException;
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
import java.time.LocalDateTime;
import java.util.*;

import static com.parunev.linkededge.model.enums.ValidValue.*;
import static com.parunev.linkededge.openai.OpenAiPrompts.*;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
import static com.parunev.linkededge.util.email.EmailPatterns.changeEmailAddress;

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

    public ProfileEmailResponse changeUserEmail(@Valid ProfileEmailRequest request){
        User user = upUtils.findUserByContextHolder();

        if (user.getEmail().equals(request.getNewEmail())){
            leLogger.warn("The email is the same as the current one used by the user");
            throw new UserProfileException(buildError("The provided email is the same as your current one!", HttpStatus.BAD_REQUEST));
        } else if (userRepository.existsByEmail(request.getNewEmail())){
            leLogger.warn("The email is already associated with a different user profile");
            throw new UserProfileException(buildError("The provided email is already associated with another user profile", HttpStatus.BAD_REQUEST));
        }

        if (!passwordEncoder.matches(request.getUserPassword(), user.getPassword())){
            leLogger.warn("The provided password does not match the user one");
            throw new UserProfileException(buildError("The provided password does not match your password.", HttpStatus.BAD_REQUEST));
        }

        leLogger.debug("Current authorization for {} & any related to him token will be removed", user.getUsername());

        deleteUserJwtTokens(user);
        ConfirmationToken token = ConfirmationToken.builder()
                .user(user)
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusMinutes(15))
                .build();
        confirmationTokenRepository.save(token);

        String encodedEmail = enDecEmail(request.getNewEmail(), "encode");

        emailSender.send(request.getNewEmail(), changeEmailAddress(user.getFullName()
         , "http://localhost:8080/edge-api/v1/profile/change-email/confirm?token=" + token.getTokenValue() + "&e=" + encodedEmail),
                "LinkedEdge: Request to change your email address");

        return ProfileEmailResponse.builder()
                .path(getCurrentRequest())
                .message("Email change request sent. Please confirm your new email address!")
                .newEmail(request.getNewEmail())
                .isEnabled(user.isEnabled())
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String enDecEmail(String email, String operation) {
        byte[] bytes;
        if(operation.equals("encode")){
            bytes = email.getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(bytes);
        } else {
            bytes = Base64.getDecoder().decode(email);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @Transactional
    public ProfileEmailResponse verifyChangeUserEmail(String token, String email){
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByTokenValue(token)
                .orElseThrow(() -> {
                    leLogger.warn("Token not found");
                    throw new RegistrationFailedException(
                            buildError("Token not found. Please ensure you have the correct token or request a new one.", HttpStatus.NOT_FOUND)
                    );
                });

        isValidToken(confirmationToken);
        User user = confirmationToken.getUser();
        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

        String decodedEmail = enDecEmail(email, "decode");
        user.setEmail(decodedEmail);

        userRepository.save(user);

        leLogger.info("The email has been changed successfully!");
        return ProfileEmailResponse.builder()
                .path(getCurrentRequest())
                .message("Your email was been changed successfully.")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ProfileResponse updateUserMfa(@Valid ProfileMfaRequest request){
        User user = upUtils.findUserByContextHolder();

        if (request.isUpdateMfa()) {
            leLogger.info("2FA Authentication enabled for user: {}", user.getUsername());
        } else {
            leLogger.info("2FA Authentication disabled for user: {}", user.getUsername());
        }

        updateMfa(user, request.isUpdateMfa());

        String message = user.isMfaEnabled()
                ? "Your 2FA Authentication has been enabled" : "Your 2FA has been disabled";

        return ProfileResponse.builder()
                .path(getCurrentRequest())
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public ProfileResponse addEducation(@Valid ProfileEducationRequest request){
       Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("Education creation started for user: {}", pair.getLeft().getUsername());

        checkIfValid(VALID_EDUCATION, request.getInstitutionName() + ", " +
               request.getFieldOfStudy() + ", " + request.getDegree());

       checkForCreditsCapacity(pair.getRight().getEducationExtraCapacity());

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

       pair.getRight().setEducationExtraCapacity(pair.getRight().getEducationExtraCapacity() - 1);
       profileRepository.save(pair.getRight());

       leLogger.info("Education saved to user profile successfully for user: {}", pair.getLeft().getUsername());
       return buildResponse(("""
               You've successfully added a new education to your profile
               Institution name: %s
                Degree: %s
                Field of study: %s
               """)
               .formatted(education.getInstitutionName(),education.getDegree(),education.getFieldOfStudy()));
    }

    public ProfileResponse returnAllUserEducations(){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        List<Education> educationsList = pair
                .getRight()
                .getEducation();

        if (educationsList.isEmpty()){
            leLogger.warn("No education records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no education records.", HttpStatus.NOT_FOUND));
        }

        List<EducationResponse> educations = map(educationsList, EducationResponse.class);

        leLogger.info("Retrieved all education records for user: {}", pair.getLeft().getUsername());
        return ProfileResponse.builder()
                .educations(educations)
                .build();
    }

    public ProfileResponse addExperience(@Valid ProfileExperienceRequest request){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("Experience creation started for user: {}", pair.getLeft().getUsername());

        checkIfValid(VALID_EXPERIENCE, "Job Title: " + request.getTitle() + ", " + request.getDescription());

        checkForCreditsCapacity(pair.getRight().getExperienceExtraCapacity());

        leLogger.info("Experience creation started for user {}", pair.getLeft().getUsername());
        Organisation organisation = Organisation.builder()
                .profile(pair.getRight())
                .name(request.getOrganisationName())
                .salesNavLink("MANUALLY ADDED")
                .build();
        organisationRepository.save(organisation);
        leLogger.info("Organisation '{}' saved successfully.", organisation.getName());

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

        pair.getRight().setExperienceExtraCapacity(pair.getRight().getExperienceExtraCapacity() - 1);
        profileRepository.save(pair.getRight());

        leLogger.info("Experience added to the profile of user: {}", pair.getLeft().getUsername());
        return buildResponse("You've successfully added a new experience to your profile%n%nOrganisation: %s%nDescription: %s%nTitle: %s%nLocation:%s%n"
                .formatted(organisation.getName(), experience.getDescription(), experience.getTitle(), experience.getLocation()));
    }

    public ProfileResponse returnAllUserExperiences(){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        List<Experience> experiencesList = pair
                .getRight()
                .getExperience();

        if (experiencesList.isEmpty()){
            leLogger.warn("No experience records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no experience records.", HttpStatus.NOT_FOUND));
        }

        List<ExperienceResponse> experiences = map(experiencesList, ExperienceResponse.class);

        leLogger.info("Retrieved all experiences for user: {}", pair.getLeft().getUsername());
        return ProfileResponse.builder()
                .experiences(experiences)
                .build();
    }

    public ProfileResponse addSkill(@Valid ProfileSkillRequest request){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        leLogger.info("ProfileSkillRequest received for user: {}", pair.getLeft().getUsername());

        leLogger.info("Checking if the skill '{}' is valid.", request.getName());
        checkForCreditsCapacity(pair.getRight().getSkillExtraCapacity());
        checkIfValid(VALID_SKILL, request.getName());
        checkForExistingSkill(pair.getRight(), request);

        leLogger.info("Skill creation started for user {}", pair.getLeft().getUsername());
        Skill skill = Skill.builder()
                .profile(pair.getRight())
                .name(request.getName())
                .numOfEndorsement(Integer.parseInt(request.getNumOfEndorsement()))
                .build();
        skillRepository.save(skill);
        leLogger.info("Skill saved successfully");

        pair.getRight().setSkillExtraCapacity(pair.getRight().getSkillExtraCapacity() - 1);
        profileRepository.save(pair.getRight());

        leLogger.info("Skill added to the profile of user: {}", pair.getLeft().getUsername());
        return buildResponse("You've successfully added a new skill to your profile%n%nSkill name: %s%nSkill level: %d%n"
                .formatted(skill.getName(), skill.getNumOfEndorsement()));
    }

    public ProfileResponse returnAllUserSkills(){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        List<Skill> skillList = pair
                .getRight()
                .getSkill();

        if (skillList.isEmpty()){
            leLogger.warn("No skill records found for user: {}", pair.getLeft().getUsername());
            throw new UserProfileException(buildError("You currently have no skill records.", HttpStatus.NOT_FOUND));
        }

        List<SkillResponse> skills = map(skillList, SkillResponse.class);

        leLogger.info("Retrieved all skills for user: {}", pair.getLeft().getUsername());
        return ProfileResponse.builder()
                .skills(skills)
                .build();
    }

    public QuestionResponse returnQuestionById(UUID questionId){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();
        Optional<Question> question = questionRepository.findById(questionId);

        if (question.isPresent()){
            Question questionToReturn = question.get();
            if (questionToReturn.getProfile().equals(pair.getRight())){
                leLogger.info("Question with ID:{} found and belongs to the specified user and profile.", questionId);
                return modelMapper.map(question, QuestionResponse.class);
            } else {
                leLogger.warn("Question with ID:{} does not belong to the specified user and profile.", questionId);
                throw new QuestionNotFoundException(buildError("This questions does not belong to your profile", HttpStatus.BAD_REQUEST));
            }
        } else {
            leLogger.warn("Question with ID:{} not found.", questionId);
            throw new QuestionNotFoundException(buildError("Question not present in the database", HttpStatus.NOT_FOUND));
        }
    }

    public Page<QuestionResponse> searchQuestions(String skill, QuestionDifficulty difficulty, UUID experienceId, UUID educationId, Pageable pageable){
        Pair<User,Profile> pair = upUtils.getUserAndProfile();

        Page<Question> questionPage = questionRepository.findAllQuestions(
                skill,
                difficulty,
                experienceId,
                educationId,
                pageable
        );

        List<Question> filteredQuestions = questionPage
                .getContent()
                .stream()
                .filter(question -> question.getProfile().equals(pair.getRight()))
                .toList();
        leLogger.info("{} questions found for the specified user and profile.", filteredQuestions.size());

        if (filteredQuestions.isEmpty()){
            throw new QuestionNotFoundException(buildError("No questions matching your criteria were found.", HttpStatus.NOT_FOUND));
        }

        return new PageImpl<>(filteredQuestions
                .stream().map(this::map).toList(), pageable, filteredQuestions.size());
    }

    private QuestionResponse map(Question question){
        return QuestionResponse.builder()
                .questionId(question.getId())
                .skillValue(question.getSkillValue())
                .questionValue(question.getQuestionValue())
                .exampleAnswer(question.getExampleAnswer())
                .difficulty(question.getDifficulty())
                .build();
    }

    private void checkForExistingSkill(Profile profile, ProfileSkillRequest request) {
        leLogger.info("Checking for an existing skill in the user profile");
        Skill skill = skillRepository.findByName(request.getName());
        if (skill != null){
            boolean isPresent = profile.getSkill().contains(skill);
            if (isPresent){
                throw new UserProfileException(buildError("The skill you're trying to enter is already in your account",HttpStatus.BAD_REQUEST));
            }
        }
    }

    private <T, R> List<R> map(List<T> sourceList, Class<R> destinationClass) {
        return sourceList.stream()
                .map(item -> modelMapper.map(item, destinationClass))
                .toList();
    }

    private void updateMfa(User user, boolean option) {
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

    private void checkForCreditsCapacity(Integer capacity) {
        if (capacity == 0){
            throw new InsufficientCapacityException(buildError(
                    "Sorry, not enough extra capacity for experiences. Consider buying more credits for your profile!",
                    HttpStatus.BAD_REQUEST
            ));
        }
    }

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

        String answer = openAi.ask(messages);

        if (answer.equals("no")) {
            leLogger.warn("Validation failed: {}", errorMessage);
            throw new UserProfileException(buildError(errorMessage, HttpStatus.BAD_REQUEST));
        } else {
            leLogger.info("Validation passed for: {}", toCheck);
        }
    }

    private void isValidToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmed() != null) {
            leLogger.warn("Token already confirmed: {}", confirmationToken.getParameters());
            throw new RegistrationFailedException(
                    buildError("The provided token has already been confirmed", HttpStatus.BAD_REQUEST));
        }

        if (confirmationToken.getExpires().isBefore(LocalDateTime.now())) {
            leLogger.warn("Token has expired: {}", confirmationToken.getParameters());
            throw new RegistrationFailedException(
                    buildError("The provided token has expired. Please request a new one", HttpStatus.BAD_REQUEST)
            );
        }
    }

    private void deleteUserJwtTokens(User user) {
        leLogger.warn("Revoking and deleting any tokens related to {}", user.getUsername());

        List<JwtToken> tokens = jwtTokenRepository.findAllValidTokenByUserId(user.getId());
        if (tokens.isEmpty()){
            leLogger.info("No tokens found to delete");
            return;
        }

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
