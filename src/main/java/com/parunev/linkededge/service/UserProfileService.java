package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.ValidValue;
import com.parunev.linkededge.model.payload.profile.*;
import com.parunev.linkededge.model.payload.profile.education.EducationResponse;
import com.parunev.linkededge.model.payload.profile.education.ProfileEducationRequest;
import com.parunev.linkededge.model.payload.profile.experience.ExperienceResponse;
import com.parunev.linkededge.model.payload.profile.experience.ProfileExperienceRequest;
import com.parunev.linkededge.model.payload.profile.skill.ProfileSkillRequest;
import com.parunev.linkededge.model.payload.profile.skill.SkillResponse;
import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.parunev.linkededge.model.enums.ValidValue.*;
import static com.parunev.linkededge.openai.OpenAiPrompts.*;
import static com.parunev.linkededge.security.CurrentUser.getCurrentUserDetails;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

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
    private final ModelMapper modelMapper;
    private final OpenAi openAi;
    private final LELogger leLogger = new LELogger(UserProfileService.class);

    public ProfileResponse updateUserMfa(@Valid ProfileMfaRequest request){
        User user = findUserByContextHolder();

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
       Pair<User,Profile> pair = getUserAndProfile();
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
        Pair<User,Profile> pair = getUserAndProfile();

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
        Pair<User,Profile> pair = getUserAndProfile();
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
        Pair<User,Profile> pair = getUserAndProfile();

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
        Pair<User,Profile> pair = getUserAndProfile();
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
        Pair<User,Profile> pair = getUserAndProfile();

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

    private Pair<User, Profile> getUserAndProfile(){
        User user = findUserByContextHolder();
        Profile profile = findProfileByUserId(user.getId());

        return Pair.of(user, profile);
    }

    private ProfileResponse buildResponse(String message){
        return ProfileResponse.builder()
                .path(getCurrentRequest())
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
    }

    private User findUserByContextHolder() {
        return userRepository.findByUsername(getCurrentUserDetails().getUsername())
                .orElseThrow(() -> {
                    leLogger.warn("User not found.");
                    throw new UserNotFoundException(buildError("User not present in the database.",HttpStatus.NOT_FOUND));
                });
    }

    private Profile findProfileByUserId(UUID id) {
        return profileRepository.findByUserId(id)
                .orElseThrow(() -> {
                    leLogger.warn("Profile not found");
                    throw new ProfileNotFoundException(buildError("Profile not present in the database",HttpStatus.NOT_FOUND));
                });
    }

    private void checkForCreditsCapacity(Integer capacity) {
        if (capacity == 0){
            throw new InsufficientCapacityException(buildError(
                    "Sorry, not enough extra capacity for experiences. Consider upgrading your profile!",
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

    private ApiError buildError(String message, HttpStatus status){
        return ApiError.builder()
                .path(getCurrentRequest())
                .error(message)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }
}
