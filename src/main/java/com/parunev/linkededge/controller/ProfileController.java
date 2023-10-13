package com.parunev.linkededge.controller;

import com.parunev.linkededge.model.enums.QuestionDifficulty;
import com.parunev.linkededge.model.payload.interview.AnswerResponse;
import com.parunev.linkededge.model.payload.interview.QuestionResponse;
import com.parunev.linkededge.model.payload.profile.ProfileMfaRequest;
import com.parunev.linkededge.model.payload.profile.ProfileResponse;
import com.parunev.linkededge.model.payload.profile.education.ProfileEducationRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailRequest;
import com.parunev.linkededge.model.payload.profile.email.ProfileEmailResponse;
import com.parunev.linkededge.model.payload.profile.experience.ProfileExperienceRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordResponse;
import com.parunev.linkededge.model.payload.profile.skill.ProfileSkillRequest;
import com.parunev.linkededge.service.UserProfileService;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.annotations.openapi.profile.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/profile")
@Tag(name = "Profile Controller", description = "API endpoints for managing user profiles and related actions.")
public class ProfileController {

    private final UserProfileService userProfileService;
    private final LELogger leLogger = new LELogger(ProfileController.class);

    @ApiChangePassword
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileChangePasswordResponse> changeUserPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for profile password change")
            @RequestBody ProfileChangePasswordRequest request){
        leLogger.info("Request to change user password");
        return new ResponseEntity<>(userProfileService.changeUserPassword(request), HttpStatus.OK);
    }

    @ApiChangeEmail
    @PostMapping("/change-email")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileEmailResponse> changeUserEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for profile email change")
            @RequestBody ProfileEmailRequest request){
        leLogger.info("Request to change user email");
        return new ResponseEntity<>(userProfileService.changeUserEmail(request), HttpStatus.OK);
    }

    @ApiChangeEmailConfirm
    @GetMapping("/change-email/confirm")
    public ResponseEntity<ProfileEmailResponse> verifyChangeUserEmail(
            @Parameter(in = ParameterIn.QUERY, name = "token", description = "The confirmation token received via email.")
            @RequestParam("token") String token,

            @Parameter(in = ParameterIn.QUERY, name = "email", description = "The new (hashed)email address to be associated with the user's account.")
            @RequestParam("e") String email){
        leLogger.info("Request to verify the change of the user email");
        return new ResponseEntity<>(userProfileService.verifyChangeUserEmail(token, email), HttpStatus.OK);
    }

    @ApiUpdateUserMfa
    @PostMapping("/mfa")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> updateUserMfa(@RequestBody ProfileMfaRequest request){
        leLogger.info("Request to update user MFA");
        return new ResponseEntity<>(userProfileService.updateUserMfa(request), HttpStatus.OK);
    }

    @ApiAddEducation
    @PostMapping("/education")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserEducation(@RequestBody ProfileEducationRequest request){
        leLogger.info("Request to add new user education");
        return new ResponseEntity<>(userProfileService.addEducation(request), HttpStatus.CREATED);
    }

    @ApiAllEducations
    @GetMapping("/educations")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserEducations(){
        leLogger.info("Request to get all user educations");
        return new ResponseEntity<>(userProfileService.returnAllUserEducations(), HttpStatus.OK);
    }

    @ApiAddExperience
    @PostMapping("/experience")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserExperience(@RequestBody ProfileExperienceRequest request){
        leLogger.info("Request to add new user experience");
        return new ResponseEntity<>(userProfileService.addExperience(request), HttpStatus.CREATED);
    }

    @ApiAllExperiences
    @GetMapping("/experiences")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserExperiences(){
        leLogger.info("Request to get all user experiences");
        return new ResponseEntity<>(userProfileService.returnAllUserExperiences(), HttpStatus.OK);
    }

    @ApiAddSkill
    @PostMapping("/skill")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserSkill(@RequestBody ProfileSkillRequest request){
        leLogger.info("Request to add new user skill");
        return new ResponseEntity<>(userProfileService.addSkill(request), HttpStatus.CREATED);
    }

    @ApiAllSkills
    @GetMapping("/skills")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserSkills(){
        leLogger.info("Request to get all user skills");
        return new ResponseEntity<>(userProfileService.returnAllUserSkills(), HttpStatus.OK);
    }

    @ApiRetrieveQuestionById
    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<QuestionResponse> getQuestionById(
            @Parameter(in = ParameterIn.PATH, name = "questionId", description = "The unique identifier of the question.")
            @PathVariable UUID questionId){
        leLogger.info("Request to get question by id");
        return new ResponseEntity<>(userProfileService.returnQuestionById(questionId), HttpStatus.OK);
    }

    @ApiAllQuestions
    @GetMapping("/questions")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<Page<QuestionResponse>> getAllQuestions(
            @Parameter(in = ParameterIn.QUERY, name = "skill", description = "Filter by skill.")
            @RequestParam(required = false, defaultValue = "") String skill,

            @Parameter(in = ParameterIn.QUERY, name = "questionDifficulty", description = "Filter by question difficulty.")
            @RequestParam(required = false, defaultValue = "") QuestionDifficulty questionDifficulty,

            @Parameter(in = ParameterIn.QUERY, name = "experienceId", description = "Filter by experience ID.")
            @RequestParam(required = false, defaultValue = "") UUID experienceId,

            @Parameter(in = ParameterIn.QUERY, name = "educationId", description = "Filter by education ID.")
            @RequestParam(required = false, defaultValue = "") UUID educationId,
            Pageable pageable
            ){
        leLogger.info("Request to retrieve all questions or categorized ones with the following parameters:" +
                "Skill {}; Difficulty: {}, ExperienceId: {}, EducationId: {}", skill, questionDifficulty, experienceId, educationId);

        return new ResponseEntity<>(userProfileService.searchQuestions(skill, questionDifficulty, experienceId, educationId, pageable), HttpStatus.OK);
    }

    @ApiAllAnswers
    @GetMapping("/answers")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<Page<AnswerResponse>> getAllAnswers(
            @Parameter(in = ParameterIn.QUERY, name = "fromDate", description = "Filter by answer creation date (from).")
            @RequestParam(required = false, defaultValue = "") LocalDate fromDate,

            @Parameter(in = ParameterIn.QUERY, name = "toDate", description = "Filter by answer creation date (to).")
            @RequestParam(required = false, defaultValue = "") LocalDate toDate,

            @Parameter(in = ParameterIn.QUERY, name = "input", description = "Filter by answer content.")
            @RequestParam(required = false, defaultValue = "") String input,
            Pageable pageable){
        leLogger.info("Request to retrieve all the answers or categorized ones with the following parameters:" +
                "fromDate: {}, toDate: {}, input: {}",fromDate, toDate, input);

        return new ResponseEntity<>(userProfileService.searchAnswers(fromDate, toDate, input, pageable), HttpStatus.OK);
    }
}
