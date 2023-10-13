package com.parunev.linkededge.model.payload.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Request payload for resending a confirmation token", description = "This payload will be used in order to generate" +
        "a new confirmation token for the user. Let's say the user forgot the confirm his profile, he'll be provided an option to request" +
        "another one.")
public class ResendTokenRequest {

    @NotBlank(message = "Please enter your email address.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "Please enter a valid email address.")
    @Schema(name = "User's email", example = "linked_edge@gmail.com", type = "String")
    private String email;
}
