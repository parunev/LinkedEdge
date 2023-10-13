package com.parunev.linkededge.model.payload.login;

import com.parunev.linkededge.util.annotations.password.PasswordConfirmation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordConfirmation
@Schema(name = "Request payload for resetting the user's password.", description = "Uses custom annotation @PasswordConfirm" +
        "to check if the password field matches the confirmPassword field")
public class ResetPasswordRequest {
    @NotBlank(message = "Please enter a password.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-])[A-Za-z\\d@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-]{7,}$",
            message = "Your password must have at least 8 characters, with a mix of uppercase, lowercase, numbers and symbols.")
    @Schema(name = "User's new password", example = "LinkedEdgeNewPassword123@!", type = "String")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    @Schema(name = "Confirmation of the user's new password", example = "LinkedEdgeNewPassword123@!", type = "String")
    private String confirmPassword;
}
