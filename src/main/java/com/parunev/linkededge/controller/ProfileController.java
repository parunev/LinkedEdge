package com.parunev.linkededge.controller;

import com.parunev.linkededge.model.payload.profile.*;
import com.parunev.linkededge.model.payload.profile.education.ProfileEducationRequest;
import com.parunev.linkededge.model.payload.profile.experience.ProfileExperienceRequest;
import com.parunev.linkededge.model.payload.profile.skill.ProfileSkillRequest;
import com.parunev.linkededge.service.UserProfileService;
import com.parunev.linkededge.util.LELogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/profile")
public class ProfileController {

    private final UserProfileService userProfileService;
    private final LELogger leLogger = new LELogger(ProfileController.class);

    @PostMapping("/mfa")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> updateUserMfa(@RequestBody ProfileMfaRequest request){
        leLogger.info("Request to update user MFA");
        return new ResponseEntity<>(userProfileService.updateUserMfa(request), HttpStatus.OK);
    }

    @PostMapping("/education")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserEducation(@RequestBody ProfileEducationRequest request){
        leLogger.info("Request to add new user education");
        return new ResponseEntity<>(userProfileService.addEducation(request), HttpStatus.CREATED);
    }

    @GetMapping("/educations")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserEducations(){
        leLogger.info("Request to get all user educations");
        return new ResponseEntity<>(userProfileService.returnAllUserEducations(), HttpStatus.OK);
    }

    @PostMapping("/experience")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserExperience(@RequestBody ProfileExperienceRequest request){
        leLogger.info("Request to add new user experience");
        return new ResponseEntity<>(userProfileService.addExperience(request), HttpStatus.CREATED);
    }

    @GetMapping("/experiences")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserExperiences(){
        leLogger.info("Request to get all user experiences");
        return new ResponseEntity<>(userProfileService.returnAllUserExperiences(), HttpStatus.OK);
    }

    @PostMapping("/skill")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> addUserSkill(@RequestBody ProfileSkillRequest request){
        leLogger.info("Request to add new user skill");
        return new ResponseEntity<>(userProfileService.addSkill(request), HttpStatus.CREATED);
    }

    @GetMapping("/skills")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<ProfileResponse> getUserSkills(){
        leLogger.info("Request to get all user skills");
        return new ResponseEntity<>(userProfileService.returnAllUserSkills(), HttpStatus.OK);
    }
}
