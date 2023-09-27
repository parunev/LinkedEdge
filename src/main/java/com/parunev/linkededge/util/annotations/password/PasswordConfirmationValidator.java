package com.parunev.linkededge.util.annotations.password;

import com.parunev.linkededge.model.payload.login.ResetPasswordRequest;
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

            return password != null && password.equals(confirmPassword);
        } else if (obj instanceof ResetPasswordRequest yourClass) {
            password = yourClass.getPassword();
            confirmPassword = yourClass.getConfirmPassword();

            return password != null && password.equals(confirmPassword);
        }

        return false;
    }
}
