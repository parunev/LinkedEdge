package com.parunev.linkededge.util.annotations.linkedin;

import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * A custom constraint validator for the {@link NameInLinkedInLink} annotation. This validator checks if the first and last names
 * in a user's LinkedIn profile link match the names provided in a data object.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
public class NameInLinkedInLinkValidator implements ConstraintValidator<NameInLinkedInLink, Object> {

    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation The annotation instance.
     */
    @Override
    public void initialize(NameInLinkedInLink constraintAnnotation) {
        // This method is intentionally empty.
    }

    /**
     * Validates the object to ensure that the first and last names in a LinkedIn profile link match the provided data.
     *
     * @param obj      The object to be validated, typically an instance of RegistrationRequest.
     * @param context  The context in which the validation is performed.
     * @return True if the validation succeeds; false if it fails.
     */
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        String firstName;
        String lastName;
        String profileLink;

        if (obj instanceof RegistrationRequest yourClass){
            // Extract data from the RegistrationRequest object.
            firstName = yourClass.getFirstName().toLowerCase();
            lastName = yourClass.getLastName().toLowerCase();
            profileLink = yourClass.getProfileLink();

            // Check if the LinkedIn profile link contains both first and last names.
            return profileLink.contains(firstName) && profileLink.contains(lastName);
        }

        return false;
    }
}
