package com.parunev.linkededge.util.annotations.password;

import com.parunev.linkededge.model.payload.login.ResetPasswordRequest;
import com.parunev.linkededge.model.payload.profile.password.ProfileChangePasswordRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator implements ConstraintValidator<PasswordConfirmation, Object> {

    @Override
    public void initialize(PasswordConfirmation constraintAnnotation) {
        // This method is intentionally empty.
    }

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

    private boolean doesPasswordsMatch(String password, String confirmPassword){
        return password != null && password.equals(confirmPassword);
    }
}
