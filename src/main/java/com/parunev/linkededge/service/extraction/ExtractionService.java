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

/**
 * The `ExtractionService` class is responsible for extracting and processing
 * profile and job-related information. It interacts with external services to
 * retrieve data and then maps it to domain objects, such as profiles and jobs.
 *
 * @see <a href="https://lix-it.com">LixApi - Scrapes various pages from LinkedIn</a>
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
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

    /**
     * Creates a Job object for a given profile based on a job ID.
     * <p>
     * This method retrieves job-related data from an external source using the provided job ID.
     * It processes the data and maps it to a Job object associated with the given profile.
     *
     * @see <a href="https://lix-it.com">LixApi - Scrapes various pages from LinkedIn</a>
     *
     * @param profile The profile to which the job is associated.
     * @param jobId   The unique identifier for the job to be created.
     * @return The created Job object, or null if the job retrieval fails.
     * @throws InvalidExtractException If there is an error during job creation, or if the HTTP
     *                                response status code is not in the 2xx range.
     */
    public Job createJob(Profile profile, String jobId) {
        leLogger.info("Attempt to create a job object for jobId: {}", jobId);

        // Retrieve job-related data from LixAPI
        Pair<HttpStatusCode, String> response = collectedData(jobId, JOB_RETRIEVAL_URL.getValue());

        Job job = null;
        if (response.getLeft().is2xxSuccessful()) {
            try {
                // Proceed with the creation of the Job object from the JSON response data.
                job = proceedWithJobCreation(response.getRight(), profile);
            } catch (Exception e) {
                // Handle exceptions and throw a custom exception.
                leLogger.error("Error while creating a job object: {} {}", e, e.getMessage());
                throw throwException(HttpStatus.BAD_REQUEST, e.getMessage());
           }
        }

        leLogger.info("Attempt successful, returning job");
        return job;
    }

    /**
     * Proceeds with the creation of a Job object from JSON response data.
     * <p>
     * This method processes the JSON response data to create a Job object associated with the provided profile.
     * It extracts relevant information from the JSON data, constructs a Job object, and saves it to the database.
     *
     * @param jsonResponse The JSON response containing job-related information.
     * @param profile      The profile to which the job is associated.
     * @return The created Job object.
     * @throws JsonProcessingException If there is an error while parsing JSON data.
     */
    private Job proceedWithJobCreation(String jsonResponse, Profile profile) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse)
                .path("jobPosting");

        // Extract job-related information and build the Job object.
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

        // Save the Job object to the database.
        jobRepository.save(job);
        leLogger.debug("Job created and saved successfully");

        // Extract and save associated JobFunctions and JobIndustries.
        List<JobFunction> functions = extractAndSaveFunctions(job, rootNode.path("formattedJobFunctions"));
        List<JobIndustry> industries = extractAndSaveIndustries(job, rootNode.path("formattedIndustries"));
        createCompanyResolution(rootNode, job);

        // Set the extracted functions and industries to the job.
        if (!functions.isEmpty()){
            job.setFunctions(functions);
        }

        if (!industries.isEmpty()){
            job.setIndustries(industries);
        }

        // Save the updated Job object.
        jobRepository.save(job);

        return job;
    }

    /**
     * Extracts and saves JobIndustry objects associated with a job from a JSON node.
     * <p>
     * This method processes a JSON node containing information about job industries. For each industry in the JSON node,
     * it creates a JobIndustry object, associates it with the provided job, and saves it to the database.
     *
     * @param job     The job to which the job industries belong.
     * @param rootNode The JSON node containing information about job industries.
     * @return A list of the saved JobIndustry objects.
     */
    private List<JobIndustry> extractAndSaveIndustries(Job job, JsonNode rootNode) {
        List<JobIndustry> toReturn = new ArrayList<>();
        if (rootNode.isArray()){
            for (JsonNode industry : rootNode){
                // Create a JobIndustry object, associate it with the job, and save it to the database.
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

    /**
     * Extracts and saves JobFunction objects associated with a job from a JSON node.
     * <p>
     * This method processes a JSON node containing information about job functions. For each function in the JSON node,
     * it creates a JobFunction object, associates it with the provided job, and saves it to the database.
     *
     * @param job     The job to which the job functions belong.
     * @param rootNode The JSON node containing information about job functions.
     * @return A list of the saved JobFunction objects.
     */
    private List<JobFunction> extractAndSaveFunctions(Job job, JsonNode rootNode) {
        List<JobFunction> toReturn = new ArrayList<>();
        if (rootNode.isArray()){
            for (JsonNode function : rootNode){

                // Create a JobFunction object, associate it with the job, and save it to the database.
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

    /**
     * Creates and saves a CompanyResolution object associated with a job based on JSON node data.
     * <p>
     * This method processes JSON data related to company details and creates a CompanyResolution object for the given job.
     * It extracts information about the company, including its name, URL, description, staff count, and headquarters.
     * It also extracts and saves specialties and industries associated with the company.
     *
     * @param rootNode The JSON node containing company details and resolution information.
     * @param job      The job to which the company resolution belongs.
     */
    private void createCompanyResolution(JsonNode rootNode, Job job) {
        // Extract the company resolution result node.
        JsonNode companyResolutionResultNode = rootNode
                .path("companyDetails")
                .path("com.linkedin.voyager.deco.jobs.web.shared.WebJobPostingCompany")
                .path("companyResolutionResult");

        // Create a formatted string representing the company's headquarters.
        String headquarters = createHeadquarters(companyResolutionResultNode.path("headquarter"));

        // Build the CompanyResolution object and save it to the database.
        CompanyResolution companyResolution = CompanyResolution.builder()
                .job(job)
                .companyUniversalName(companyResolutionResultNode.get("universalName") != null ? companyResolutionResultNode.get("universalName").asText() : NOT_PRESENT.getValue())
                .companyName(companyResolutionResultNode.get(NAME.getValue())!= null ? companyResolutionResultNode.get(NAME.getValue()).asText() : NOT_PRESENT.getValue())
                .companyUrl(companyResolutionResultNode.get("url")!= null ? companyResolutionResultNode.get("url").asText() : NOT_PRESENT.getValue())
                .companyDescription(companyResolutionResultNode.get(DESCRIPTION.getValue())!= null ? companyResolutionResultNode.get(DESCRIPTION.getValue()).asText() : NOT_PRESENT.getValue())
                .companyStaffCount(companyResolutionResultNode.get("staffCount")!= null ? companyResolutionResultNode.get("staffCount").asInt() : 0)
                .companyHeadquarter(headquarters)
                .build();

        // Save the CompanyResolution object to the database.
        companyResolutionRepository.save(companyResolution);

        // Extract and save company specialties and industries.
        extractCompanySpecialties(companyResolution, companyResolutionResultNode.path("specialities"));
        extractCompanyIndustries(companyResolution, companyResolutionResultNode.path("industries"));
        leLogger.info("Created and saved company resolutions for job {}", job.getId());
    }

    /**
     * Extracts and saves CompanyIndustry objects associated with a CompanyResolution from a JSON node.
     * <p>
     * This method processes a JSON node containing information about company industries. For each industry in the JSON node,
     * it creates a CompanyIndustry object, associates it with the provided CompanyResolution, and saves it to the database.
     *
     * @param companyResolution The CompanyResolution to which the company industries belong.
     * @param industries        The JSON node containing information about company industries.
     */
    private void extractCompanyIndustries(CompanyResolution companyResolution, JsonNode industries) {
        if (industries.isArray()){
            for (JsonNode industry : industries){
                // Create a CompanyIndustry object, associate it with the CompanyResolution, and save it to the database.
                CompanyIndustry companyIndustry = CompanyIndustry.builder()
                        .companyResolution(companyResolution)
                        .industryName(industry.asText())
                        .build();
                companyIndustryRepository.save(companyIndustry);
            }
        }
        leLogger.info("Extracted and saved {} industries for company resolution {}", industries.size(), companyResolution.getId());
    }

    /**
     * Extracts and saves CompanySpecialty objects associated with a CompanyResolution from a JSON node.
     * <p>
     * This method processes a JSON node containing information about company specialties. For each specialty in the JSON node,
     * it creates a CompanySpecialty object, associates it with the provided CompanyResolution, and saves it to the database.
     *
     * @param companyResolution The CompanyResolution to which the company specialties belong.
     * @param specialities      The JSON node containing information about company specialties.
     */
    private void extractCompanySpecialties(CompanyResolution companyResolution, JsonNode specialities) {
        if (specialities.isArray()){
            for (JsonNode specialty : specialities){
                // Create a CompanySpecialty object, associate it with the CompanyResolution, and save it to the database.
                CompanySpecialty companySpecialty = CompanySpecialty.builder()
                        .companyResolution(companyResolution)
                        .specialtyName(specialty.asText())
                        .build();
                companySpecialtyRepository.save(companySpecialty);
            }
        }
        leLogger.info("Extracted and saved {} specialties for company resolution {}", specialities.size(), companyResolution.getId());
    }

    /**
     * Creates a formatted headquarters string based on a JSON node.
     * <p>
     * This method takes a JSON node containing information about the headquarters of a company and creates a formatted
     * string representation of the headquarters, including the country, city, and postal code.
     *
     * @param node The JSON node containing information about the company's headquarters.
     * @return A formatted headquarters string.
     */
    private String createHeadquarters(JsonNode node) {
        // Build a formatted headquarters string with country, city, and postal code information.
        String headquarter = "Country: %s, ".formatted(node.get("country") != null ? node.get("country").asText() : NOT_PRESENT.getValue()) +
                "City: %s, ".formatted(node.get("city")!= null ? node.get("city").asText() : NOT_PRESENT.getValue()) +
                "Postal code: %s".formatted(node.get("postalCode")!= null ? node.get("postalCode").asText() : NOT_PRESENT.getValue());

        leLogger.debug("Created headquarters: {}", headquarter);
        return headquarter;
    }


    /**
     * Creates and saves a user profile based on JSON data from a LinkedIn profile.
     * <p>
     * This method initiates the creation of a user profile by collecting and processing JSON data from a LinkedIn profile.
     * It first retrieves profile data through an HTTP request and then proceeds to create the user profile using the
     * processed JSON data. The resulting user profile includes information about the user's education, experience, skills,
     * and other details.
     *
     * @param user The user for whom the profile is being created.
     * @throws JSONException If an exception occurs during JSON data processing.
     */
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

    /**
     * Proceeds with the creation of a user profile based on processed JSON data.
     * <p>
     * This method takes processed JSON data from a LinkedIn profile and creates a user profile object. It extracts
     * information about the user, including their name, location, description, image URL, sales navigation link, and
     * account link. The resulting user profile also includes details about the user's skills, experiences, and education.
     * All of this information is saved to the database.
     *
     * @param obj  The JSON object containing processed profile information.
     * @param user The user for whom the profile is being created.
     * @throws JSONException If an exception occurs during JSON data processing.
     */
    private void proceedWithProfileCreation(JSONObject obj, User user) throws JSONException {
        leLogger.info("Starting extraction of profile information.");
        Profile profile = Profile.builder()
                .user(user)
                .credits(3)
                .jobCredits(1)
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

        // Extract and save user's skills, experiences, and education.
        List<Skill> skills = extractSkills(obj, profile);
        List<Experience> experiences = extractExperience(obj, profile);
        List<Education> educations = extractEducation(obj, profile);
        profile.setSkill(skills);
        profile.setExperience(experiences);
        profile.setEducation(educations);
        profileRepository.save(profile);
    }

    /**
     * Extracts and saves Skill objects from processed JSON data.
     * <p>
     * This method processes JSON data to extract information about a user's skills. It iterates through a JSON array of
     * skills, creates Skill objects for each skill, and associates them with the provided user profile. The Skill objects
     * are then saved to the database.
     *
     * @param jsonObject The JSON object containing processed user profile data.
     * @param profile    The user profile to which the skills belong.
     * @return A list of extracted Skill objects.
     * @throws JSONException If an exception occurs during JSON data processing.
     */
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

    /**
     * Extracts and saves Experience objects from processed JSON data.
     * <p>
     * This method processes JSON data to extract information about a user's professional experiences. It iterates through
     * a JSON array of experiences, creates Experience objects for each experience, and associates them with the provided
     * user profile. Additionally, it extracts and associates details about the organization where the user gained
     * experience. The Experience and Organization objects are then saved to the database.
     *
     * @param jsonObject The JSON object containing processed user profile data.
     * @param profile    The user profile to which the experiences belong.
     * @return A list of extracted Experience objects.
     * @throws JSONException If an exception occurs during JSON data processing.
     */
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

    /**
     * Extracts and saves Education objects from processed JSON data.
     * <p>
     * This method processes JSON data to extract information about a user's educational background. It iterates through a
     * JSON array of education records, creates Education objects for each record, and associates them with the provided
     * user profile. The Education objects represent details such as institution name, degree, field of study, and dates
     * of study. These Education objects are then saved to the database.
     *
     * @param jsonObject The JSON object containing processed user profile data.
     * @param profile    The user profile to which the education records belong.
     * @return A list of extracted Education objects.
     * @throws JSONException If an exception occurs during JSON data processing.
     */
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

    /**
     * Collects data from an external endpoint using an HTTP GET request.
     * <p>
     * This method sends an HTTP GET request to a specified endpoint with the provided link and necessary headers,
     * including an authorization key. It collects the response from the endpoint and returns a Pair containing
     * the HTTP status code and the response body as a String.
     *
     * @see <a href="https://lix-it.com">LixApi - Scrapes various pages from LinkedIn</a>
     *
     * @param link     The link or path to the specific data resource.
     * @param endpoint The base endpoint URL to which the link is appended.
     * @return A Pair containing the HTTP status code and the response body.
     */
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
