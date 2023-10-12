package com.parunev.linkededge.model.payload.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Request payload for user verification", description = "Used in the multi-factor authentication process.")
public class VerificationRequest {

    @Schema(name = "User's username", example = "linked_edge", type = "String")
    private String username;

    @Schema(name = "Verification code for multi-factor authentication", example = "566233", type = "String")
    private String code;
}
