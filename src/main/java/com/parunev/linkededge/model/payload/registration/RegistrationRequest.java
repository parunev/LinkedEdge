package com.parunev.linkededge.model.payload.registration;

import com.parunev.linkededge.util.annotations.linkedin.NameInLinkedInLink;
import com.parunev.linkededge.util.annotations.password.PasswordConfirmation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@NameInLinkedInLink
@PasswordConfirmation
@Schema(name = "Request payload for user registration", description = "Uses custom annotation @PasswordConfirm" +
        "to check if the password field matches the confirm field")
public class RegistrationRequest {

    @NotBlank(message = "Please enter your username")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{6,29}$",
    message = "Your username must start with alphabet letter, must be between 7 and 30 symbols. Only underscore allowed!")
    @Schema(name = "User's username", example = "linked_edge", type = "String")
    private String username;

    @NotBlank(message = "Please enter your email address.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Please enter a valid email address.")
    @Schema(name = "User's email address", example = "linked_edge@gmail.com", type = "String")
    private String email;

    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-])[A-Za-z\\d@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-]{7,}$",
            message = "Your password must have at least 8 characters, with a mix of uppercase, lowercase, numbers and symbols.")
    @Schema(name = "User's password", example = "Linked123!@", type = "String", description = "The password will be encoded with BCrypt")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    @Schema(name = "Confirmation of the password", example = "Linked123!@", type = "String", description = "The confirm password needs to match with the password")
    private String confirm;

    @NotBlank(message = "Please enter your first name.")
    @Size(max = 50, message = "First name must be less than 50 characters long.")
    @Pattern(regexp = "^[a-zA-Zà-üÀ-Ü]+$", message = "First name should contain only letters")
    @Schema(name = "User's first name", example = "Martin", type = "String")
    private String firstName;

    @NotBlank(message = "Please enter your last name.")
    @Size(max = 50, message = "Last name must be less than 50 characters long.")
    @Pattern(regexp = "^[a-zA-Zà-üÀ-Ü]+$", message = "Last name should contain only letters")
    @Schema(name = "User's last name", example = "Parunev", type = "String")
    private String lastName;

    @NotBlank(message = "Please enter your LinkedIn profile")
    @Pattern(regexp = "https://www\\.linkedin\\.com/in/.*", message = "Not a valid LinkedIn link")
    @Schema(name = "User's LinkedIn Profile link", example = "www.linkedin.com/in/martin-parunev-49006425b", type = "String",
            description = "Validated through pattern and custom annotation validator @NameInLinkedInLink which checks if the users first name" +
                    "and last name are inside the link")
    private String profileLink;
}
