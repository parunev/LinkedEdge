package com.parunev.linkededge.model.payload.registration;

import com.parunev.linkededge.util.annotations.linkedin.NameInLinkedInLink;
import com.parunev.linkededge.util.annotations.password.PasswordConfirmation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@NameInLinkedInLink
@PasswordConfirmation
public class RegistrationRequest {

    @NotBlank(message = "Please enter your username")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{6,29}$",
    message = "Your username must start with alphabet letter, must be between 7 and 30 symbols. Only underscore allowed!")
    private String username;

    @NotBlank(message = "Please enter your email address.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-])[A-Za-z\\d@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-]{7,}$",
            message = "Your password must have at least 8 characters, with a mix of uppercase, lowercase, numbers and symbols.")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirm;

    @NotBlank(message = "Please enter your first name.")
    @Size(max = 50, message = "First name must be less than 50 characters long.")
    @Pattern(regexp = "^[a-zA-Zà-üÀ-Ü]+$", message = "First name should contain only letters")
    private String firstName;

    @NotBlank(message = "Please enter your last name.")
    @Size(max = 50, message = "Last name must be less than 50 characters long.")
    @Pattern(regexp = "^[a-zA-Zà-üÀ-Ü]+$", message = "Last name should contain only letters")
    private String lastName;

    @NotBlank(message = "Please enter your LinkedIn profile")
    @Pattern(regexp = "https://www\\.linkedin\\.com/in/.*", message = "Not a valid LinkedIn link")
    private String profileLink;
}
