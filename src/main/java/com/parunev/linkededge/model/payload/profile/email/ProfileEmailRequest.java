package com.parunev.linkededge.model.payload.profile.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Request payload for changing the user's email address in the profile.")
public class ProfileEmailRequest {
    @NotBlank(message = "Please enter your new email address.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Please enter a valid new email address.")
    @Schema(name = "New email address", example = "linked_edge_new@gmail.com", type = "String")
    private String newEmail;

    @NotBlank(message = "Please enter your account password")
    @Schema(name = "User's current password", example = "LinkedEdge123!@", type = "String")
    private String userPassword;

}
