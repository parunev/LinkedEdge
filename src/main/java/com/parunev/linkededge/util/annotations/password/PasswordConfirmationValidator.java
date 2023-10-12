package com.parunev.linkededge.util.annotations.password;

import com.parunev.linkededge.model.payload.login.ResetPasswordRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
/**
 * The `PasswordConfirmationValidator` class implements the validation logic for the `PasswordConfirmation` annotation.
 * This validator ensures that two password fields match during user registration or password update processes. It is used
 * in conjunction with the `PasswordConfirmation` annotation to compare password fields and determine if they match.
 *
 * @see PasswordConfirmation
 * @author Martin Parunev
 * @date October 12, 2023
 */
public class PasswordConfirmationValidator implements ConstraintValidator<PasswordConfirmation, Object> {

    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation The `PasswordConfirmation` annotation instance (not used in this validator).
     */
    @Override
    public void initialize(PasswordConfirmation constraintAnnotation) {
        // This method is intentionally empty.
    }

    /**
     * Validates whether the password fields within the annotated class match.
     *
     * @param obj      The object containing password fields to be validated.
     * @param context  The validation context.
     * @return True if the password fields match; otherwise, false.
     */
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        String password;
        String confirmPassword;

        if (obj instanceof RegistrationRequest yourClass) {
            password = yourClass.getPassword();
            confirmPassword = yourClass.getConfirm();

            return doesPasswordsMatch(password, confirmPassword);
        } else if (obj instanceof ResetPasswordRequest yourClass) {
            password = yourClass.getPassword();
            confirmPassword = yourClass.getConfirmPassword();

            return doesPasswordsMatch(password, confirmPassword);
        } else if (obj instanceof ProfileChangePasswordRequest yourClass){
            password = yourClass.getNewPassword();
            confirmPassword = yourClass.getConfirmNewPassword();

            return doesPasswordsMatch(password, confirmPassword);
        }

        return false;
    }

    /**
     * Compares two password values to determine if they match.
     *
     * @param password       The password to be compared.
     * @param confirmPassword The confirmation password to be compared.
     * @return True if the password and confirmation password match; otherwise, false.
     */
    private boolean doesPasswordsMatch(String password, String confirmPassword){
        return password != null && password.equals(confirmPassword);
    }
}
