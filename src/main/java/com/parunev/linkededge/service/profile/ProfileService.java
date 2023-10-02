package com.parunev.linkededge.service.profile;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.InvalidExtractException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.parunev.linkededge.service.profile.ProfileConstants.*;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@Service
@Validated
@RequiredArgsConstructor
public class ProfileService {

    @Value("${lix.authorization.key}")
    private String lixKey;
    private final RestTemplate restTemplate;
    private final ProfileRepository profileRepository;
    private final ExperienceRepository experienceRepository;
    private final OrganisationRepository organisationRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final LELogger leLogger = new LELogger(ProfileService.class);

    public void createProfile(User user) throws JSONException {
        leLogger.info("Attempt to create user profile");
        Pair<HttpStatusCode, String> response = collectedData(user.getLinkedInProfile());
        JSONObject jsonObject = new JSONObject(response.getRight());

        if (response.getLeft().is2xxSuccessful()) {
            try {
                proceedWithCreation(jsonObject, user);
            } catch (Exception e) {
                throw throwException(response.getLeft(), e.getMessage());
            }
        }
    }

    private void proceedWithCreation(JSONObject obj, User user) throws JSONException {
        leLogger.info("Starting extraction of information.");
        Profile profile = Profile.builder()
                .user(user)
                .credits(3)
                .educationExtraCapacity(10)
                .experienceExtraCapacity(10)
                .skillExtraCapacity(10)
                .fullName(obj.has(NAME.getValue()) ? obj.getString(NAME.getValue()) : NOT_PRESENT.getValue())
                .location(obj.has(LOCATION.getValue()) ? obj.getString(LOCATION.getValue()) : NOT_PRESENT.getValue())
                .description(obj.has(DESCRIPTION.getValue()) ? obj.getString(DESCRIPTION.getValue()) : NOT_PRESENT.getValue())
                .imageUrl(obj.has(IMAGE.getValue()) ? obj.getString(IMAGE.getValue()) : NOT_PRESENT.getValue())
                .salesNavLink(obj.has(SALES_NAV_LINK.getValue()) ? obj.getString(SALES_NAV_LINK.getValue()) : NOT_PRESENT.getValue())
                .accountLink(obj.has(ACCOUNT_LINK.getValue()) ? obj.getString(ACCOUNT_LINK.getValue()) : NOT_PRESENT.getValue())
                .build();
        profileRepository.save(profile);

        List<Skill> skills = extractSkills(obj, profile);
        List<Experience> experiences = extractExperience(obj, profile);
        List<Education> educations = extractEducation(obj, profile);
        profile.setSkill(skills);
        profile.setExperience(experiences);
        profile.setEducation(educations);
        profileRepository.save(profile);
    }

    private List<Skill> extractSkills(JSONObject jsonObject, Profile profile) throws JSONException {
        leLogger.info("Starting extraction of skills information.");
        List<Skill> skillList = new ArrayList<>();

        JSONArray skillsArray = jsonObject.getJSONArray("skills");

        if (skillsArray.length() == 0){
            return Collections.emptyList();
        }

        for (int i = 0; i < skillsArray.length(); i++) {
            JSONObject obj = skillsArray.getJSONObject(i);
            Skill skill = Skill.builder()
                    .profile(profile)
                    .name(obj.has(NAME.getValue()) ? obj.getString(NAME.getValue()) : NOT_PRESENT.getValue())
                    .numOfEndorsement(obj.has("numOfEndorsement") ? Integer.parseInt(obj.getString("numOfEndorsement")) : 0)
                    .build();
            skillRepository.save(skill);
            skillList.add(skill);
        }

       return skillList;
    }

    private List<Experience> extractExperience(JSONObject jsonObject, Profile profile) throws JSONException {
        leLogger.info("Starting extraction of experience information.");
        List<Experience> experienceList = new ArrayList<>();

        JSONArray experienceArray = jsonObject.getJSONArray("experience");
        if (experienceArray.length() == 0){
            return Collections.emptyList();
        }

        for (int i = 0; i < experienceArray.length(); i++) {
            JSONObject expObj = experienceArray.getJSONObject(i);

            JSONObject orgObj = expObj.getJSONObject("organisation");
            Organisation organisation = Organisation.builder()
                    .profile(profile)
                    .name(orgObj.has(NAME.getValue())
                            ? orgObj.getString(NAME.getValue()) : NOT_PRESENT.getValue())
                    .salesNavLink(orgObj.has(SALES_NAV_LINK.getValue())
                            ? orgObj.getString(SALES_NAV_LINK.getValue()) : NOT_PRESENT.getValue())
                    .build();
            organisationRepository.save(organisation);

            Experience experience = Experience.builder()
                    .profile(profile)
                    .title(expObj.has("title")
                            ? expObj.getString("title") : NOT_PRESENT.getValue())
                    .dateStarted(expObj.has(DATE_STARTED.getValue()) ? expObj.getString(DATE_STARTED.getValue()) : NOT_PRESENT.getValue())
                    .dateEnded(expObj.has(DATE_ENDED.getValue()) ? expObj.optString(DATE_ENDED.getValue()) : NOT_PRESENT.getValue())
                    .location(expObj.has(LOCATION.getValue()) ? expObj.getString(LOCATION.getValue()) : NOT_PRESENT.getValue())
                    .organisation(organisation)
                    .build();
            experienceRepository.save(experience);
            experienceList.add(experience);
        }

        return experienceList;
    }

    private List<Education> extractEducation(JSONObject jsonObject, Profile profile) throws JSONException {
        leLogger.info("Starting extraction of education information.");

        List<Education> educationList = new ArrayList<>();
        JSONArray educationArray = jsonObject.getJSONArray("education");
        if (educationArray.length() == 0){
            return Collections.emptyList();
        }

        for (int i = 0; i < educationArray.length(); i++) {
            JSONObject eduObj = educationArray.getJSONObject(i);
            Education education = Education.builder()
                    .profile(profile)
                    .institutionName(eduObj.has(INSTITUTION_NAME.getValue()) ? eduObj.getString(INSTITUTION_NAME.getValue()) : NOT_PRESENT.getValue())
                    .degree(eduObj.has(DEGREE.getValue()) ? eduObj.getString(DEGREE.getValue()) : NOT_PRESENT.getValue())
                    .fieldOfStudy(eduObj.has(FIELD_OF_STUDY.getValue()) ? eduObj.getString(FIELD_OF_STUDY.getValue()) : NOT_PRESENT.getValue())
                    .dateStarted(eduObj.has(DATE_STARTED.getValue()) ? eduObj.getString(DATE_STARTED.getValue()) : NOT_PRESENT.getValue())
                    .dateEnded(eduObj.has(DATE_ENDED.getValue()) ? eduObj.getString(DATE_ENDED.getValue()) : NOT_PRESENT.getValue())
                    .build();
            educationRepository.save(education);
            educationList.add(education);
        }

        return educationList;
    }

    private Pair<HttpStatusCode, String> collectedData(String account){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", lixKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    PROFILE_RETRIEVAL_URL.getValue() + account,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            leLogger.info("Profile retrieval successful for account: {}", account);
            return Pair.of(response.getStatusCode(), response.getBody());

        } catch (HttpStatusCodeException ex) {
            leLogger.error("Profile retrieval failed, exception: {}. Email: {}. Status code: {}, Response body: {}",
                    ex, account, ex.getStatusCode(), ex.getResponseBodyAsString());
            return Pair.of(ex.getStatusCode(), ex.getResponseBodyAsString());

        } catch (Exception e) {
            leLogger.error("An error occurred while retrieving profile exception: {}. Email: {}.", e , account);
            return Pair.of(HttpStatusCode.valueOf(500), "An error occurred on the server." +
                    " Should this error persist, please contact our technical team.");
        }
    }

    private RuntimeException throwException(HttpStatusCode status, String response) {
        throw new InvalidExtractException(ApiError.builder()
                .path(getCurrentRequest())
                .error(response)
                .status(HttpStatus.valueOf(status.value()))
                .timestamp(LocalDateTime.now())
                .build());
    }
}
