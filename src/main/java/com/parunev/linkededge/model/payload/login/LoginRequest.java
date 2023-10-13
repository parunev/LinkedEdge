package com.parunev.linkededge.model.payload.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Request payload for user login.")
public class LoginRequest {

    @NotBlank(message = "Please enter your username")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{6,29}$")
    @Schema(name = "User's username", example = "linked_edge", type = "String", description = "The username which was chosen by the user" +
            "upon the registration")
    private String username;

    @NotBlank(message = "Please enter your password.")
    @Schema(name = "User's password", type = "String", description = "The password which was chosen by the user" +
            "upon the registration")
    private String password;
}
