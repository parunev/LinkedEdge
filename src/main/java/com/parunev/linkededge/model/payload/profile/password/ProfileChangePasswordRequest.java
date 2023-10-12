package com.parunev.linkededge.model.payload.profile.password;

import com.parunev.linkededge.util.annotations.password.PasswordConfirmation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordConfirmation
@Schema(name = "Request payload for changing the user's password", description = "This operation is going to happen inside the" +
        "user's profile. It uses the custom annotation @PasswordConfirmation to check if the new passwords match")
public class ProfileChangePasswordRequest {

    @NotBlank(message = "Please enter your current password")
    @Schema(name = "User's old password", example = "LinkedEdge123!@", type = "String")
    private String oldPassword;

    @NotBlank(message = "Please enter your new password")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-])[A-Za-z\\d@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-]{7,}$",
            message = "Your password must have at least 8 characters, with a mix of uppercase, lowercase, numbers and symbols.")
    @Schema(name = "User's new password", example = "LinkedEdge123456!@", type = "String")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password")
    @Schema(name = "Confirmation of the new password", example = "LinkedEdge123456!@", type = "String")
    private String confirmNewPassword;
}
