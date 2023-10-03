package com.parunev.linkededge.model.payload.profile.email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileEmailRequest {
    @NotBlank(message = "Please enter your new email address.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Please enter a valid new email address.")
    private String newEmail;

    @NotBlank(message = "Please enter your account password")
    private String userPassword;

}
