package com.parunev.linkededge.model.payload.profile.password;

import com.parunev.linkededge.util.annotations.password.PasswordConfirmation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordConfirmation
public class ProfileChangePasswordRequest {

    @NotBlank(message = "Please enter your current password")
    private String oldPassword;

    @NotBlank(message = "Please enter your new password")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-])[A-Za-z\\d@$!%*?&.,#\"':;><\\\\/|\\[\\]€£~{}()+=^_-]{7,}$",
            message = "Your password must have at least 8 characters, with a mix of uppercase, lowercase, numbers and symbols.")
    private String newPassword;

    @NotBlank(message = "Please confirm your new password")
    private String confirmNewPassword;
}
