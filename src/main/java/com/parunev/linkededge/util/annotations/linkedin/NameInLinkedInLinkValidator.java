package com.parunev.linkededge.util.annotations.linkedin;

import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameInLinkedInLinkValidator implements ConstraintValidator<NameInLinkedInLink, Object> {


    @Override
    public void initialize(NameInLinkedInLink constraintAnnotation) {
        // This method is intentionally empty.
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        String firstName;
        String lastName;
        String profileLink;

        if (obj instanceof RegistrationRequest yourClass){
            firstName = yourClass.getFirstName().toLowerCase();
            lastName = yourClass.getLastName().toLowerCase();
            profileLink = yourClass.getProfileLink();

            return profileLink.contains(firstName) && profileLink.contains(lastName);
        }

        return false;
    }
}
