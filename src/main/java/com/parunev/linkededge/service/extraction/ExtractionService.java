package com.parunev.linkededge.service.extraction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.job.*;
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

import static com.parunev.linkededge.service.extraction.ExtractionConstants.*;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@Service
@Validated
@RequiredArgsConstructor
public class ExtractionService {

    @Value("${lix.authorization.key}")
    private String lixKey;
    private final RestTemplate restTemplate;
    private final ProfileRepository profileRepository;

    // PROFILE RELATED
    private final ExperienceRepository experienceRepository;
    private final OrganisationRepository organisationRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;

    // JOB RELATED
    private final CompanyIndustryRepository companyIndustryRepository;
    private final CompanyResolutionRepository companyResolutionRepository;
    private final CompanySpecialtyRepository companySpecialtyRepository;
    private final JobRepository jobRepository;
    private final JobFunctionRepository jobFunctionRepository;
    private final JobIndustryRepository jobIndustryRepository;

    private final LELogger leLogger = new LELogger(ExtractionService.class);

    public Job createJob(Profile profile, String jobId) {
        leLogger.info("Attempt to create a job object for jobId: {}", jobId);
        Pair<HttpStatusCode, String> response = collectedData(jobId, JOB_RETRIEVAL_URL.getValue());

        Job job = null;
        if (response.getLeft().is2xxSuccessful()) {
            try {
                job = proceedWithJobCreation(response.getRight(), profile);
            } catch (Exception e) {
                leLogger.error("Error while creating a job object: {} {}", e, e.getMessage());
                throw throwException(HttpStatus.BAD_REQUEST, e.getMessage());
           }
        }

        leLogger.info("Attempt successful, returning job");
        return job;
    }

    private Job proceedWithJobCreation(String jsonResponse, Profile profile) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse)
                .path("jobPosting");

        Job job = Job.builder()
                .profile(profile)
                .jobDescription(rootNode.path(DESCRIPTION.getValue()).get("text") != null ? rootNode.path(DESCRIPTION.getValue()).get("text").asText() : NOT_PRESENT.getValue())
                .employmentStatus(rootNode.path("employmentStatusResolutionResult").get("localizedName")!= null ?
                        rootNode.path("employmentStatusResolutionResult").get("localizedName").asText() : NOT_PRESENT.getValue())
                .jobTitle(rootNode.get("title") != null ? rootNode.get("title").asText() : NOT_PRESENT.getValue())
                .location(rootNode.get("formattedLocation") != null ? rootNode.get("formattedLocation").asText() : NOT_PRESENT.getValue())
                .jobPostingUrl(rootNode.get("jobPostingUrl") != null ? rootNode.get("jobPostingUrl").asText() : NOT_PRESENT.getValue())
                .jobPostingId(rootNode.get("jobPostingId") != null ? rootNode.get("jobPostingId").asText() : NOT_PRESENT.getValue())
                .build();
        jobRepository.save(job);
        leLogger.debug("Job created and saved successfully");

        List<JobFunction> functions = extractAndSaveFunctions(job, rootNode.path("formattedJobFunctions"));
        List<JobIndustry> industries = extractAndSaveIndustries(job, rootNode.path("formattedIndustries"));
        createCompanyResolution(rootNode, job);

        if (!functions.isEmpty()){
            job.setFunctions(functions);
        }

        if (!industries.isEmpty()){
            job.setIndustries(industries);
        }

        jobRepository.save(job);

        return job;
    }

    private List<JobIndustry> extractAndSaveIndustries(Job job, JsonNode rootNode) {
        List<JobIndustry> toReturn = new ArrayList<>();
        if (rootNode.isArray()){
            for (JsonNode industry : rootNode){
                JobIndustry jobIndustry = JobIndustry.builder()
                        .job(job)
                        .jobIndustryName(industry.asText())
                        .build();
                jobIndustryRepository.save(jobIndustry);
                toReturn.add(jobIndustry);
            }
        }
        leLogger.info("Extracted and saved {} industries for job {}", toReturn.size(), job.getId());
        return toReturn;
    }

    private List<JobFunction> extractAndSaveFunctions(Job job, JsonNode rootNode) {
        List<JobFunction> toReturn = new ArrayList<>();
        if (rootNode.isArray()){
            for (JsonNode function : rootNode){
                JobFunction jobFunction = JobFunction.builder()
                        .job(job)
                        .jobFunctionName(function.asText())
                        .build();
                jobFunctionRepository.save(jobFunction);
                toReturn.add(jobFunction);
            }
        }
        leLogger.info("Extracted and saved {} functions for job {}", toReturn.size(), job.getId());
        return toReturn;
    }

    private void createCompanyResolution(JsonNode rootNode, Job job) {
        JsonNode companyResolutionResultNode = rootNode
                .path("companyDetails")
                .path("com.linkedin.voyager.deco.jobs.web.shared.WebJobPostingCompany")
                .path("companyResolutionResult");

        String headquarters = createHeadquarters(companyResolutionResultNode.path("headquarter"));

        CompanyResolution companyResolution = CompanyResolution.builder()
                .job(job)
                .companyUniversalName(companyResolutionResultNode.get("universalName") != null ? companyResolutionResultNode.get("universalName").asText() : NOT_PRESENT.getValue())
                .companyName(companyResolutionResultNode.get(NAME.getValue())!= null ? companyResolutionResultNode.get(NAME.getValue()).asText() : NOT_PRESENT.getValue())
                .companyUrl(companyResolutionResultNode.get("url")!= null ? companyResolutionResultNode.get("url").asText() : NOT_PRESENT.getValue())
                .companyDescription(companyResolutionResultNode.get(DESCRIPTION.getValue())!= null ? companyResolutionResultNode.get(DESCRIPTION.getValue()).asText() : NOT_PRESENT.getValue())
                .companyStaffCount(companyResolutionResultNode.get("staffCount")!= null ? companyResolutionResultNode.get("staffCount").asInt() : 0)
                .companyHeadquarter(headquarters)
                .build();
        companyResolutionRepository.save(companyResolution);

        extractCompanySpecialties(companyResolution, companyResolutionResultNode.path("specialities"));
        extractCompanyIndustries(companyResolution, companyResolutionResultNode.path("industries"));
        leLogger.info("Created and saved company resolutions for job {}", job.getId());
    }

    private void extractCompanyIndustries(CompanyResolution companyResolution, JsonNode industries) {
        if (industries.isArray()){
            for (JsonNode industry : industries){
                CompanyIndustry companyIndustry = CompanyIndustry.builder()
                        .companyResolution(companyResolution)
                        .industryName(industry.asText())
                        .build();
                companyIndustryRepository.save(companyIndustry);
            }
        }
        leLogger.info("Extracted and saved {} industries for company resolution {}", industries.size(), companyResolution.getId());
    }

    private void extractCompanySpecialties(CompanyResolution companyResolution, JsonNode specialities) {
        if (specialities.isArray()){
            for (JsonNode specialty : specialities){
                CompanySpecialty companySpecialty = CompanySpecialty.builder()
                        .companyResolution(companyResolution)
                        .specialtyName(specialty.asText())
                        .build();
                companySpecialtyRepository.save(companySpecialty);
            }
        }
        leLogger.info("Extracted and saved {} specialties for company resolution {}", specialities.size(), companyResolution.getId());
    }

    private String createHeadquarters(JsonNode node) {
        String headquarter = "Country: %s, ".formatted(node.get("country") != null ? node.get("country").asText() : NOT_PRESENT.getValue()) +
                "City: %s, ".formatted(node.get("city")!= null ? node.get("city").asText() : NOT_PRESENT.getValue()) +
                "Postal code: %s".formatted(node.get("postalCode")!= null ? node.get("postalCode").asText() : NOT_PRESENT.getValue());

        leLogger.debug("Created headquarters: {}", headquarter);
        return headquarter;
    }


    public void createProfile(User user) throws JSONException {
        leLogger.info("Attempt to create user profile");
        Pair<HttpStatusCode, String> response = collectedData(user.getLinkedInProfile(), PROFILE_RETRIEVAL_URL.getValue());
        JSONObject jsonObject = new JSONObject(response.getRight());

        if (response.getLeft().is2xxSuccessful()) {
            try {
                proceedWithProfileCreation(jsonObject, user);
            } catch (Exception e) {
                throw throwException(response.getLeft(), e.getMessage());
            }
        }
    }

    private void proceedWithProfileCreation(JSONObject obj, User user) throws JSONException {
        leLogger.info("Starting extraction of profile information.");
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

    private Pair<HttpStatusCode, String> collectedData(String link, String endpoint){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", lixKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint + link,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            leLogger.info("Profile retrieval successful for link: {}", link);
            return Pair.of(response.getStatusCode(), response.getBody());

        } catch (HttpStatusCodeException ex) {
            leLogger.error("Profile retrieval failed, exception: {}. Profile: {}. Status code: {}, Response body: {}",
                    ex, link, ex.getStatusCode(), ex.getResponseBodyAsString());
            return Pair.of(ex.getStatusCode(), ex.getResponseBodyAsString());

        } catch (Exception e) {
            leLogger.error("An error occurred while retrieving profile exception: {}. Profile link: {}.", e , link);
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
