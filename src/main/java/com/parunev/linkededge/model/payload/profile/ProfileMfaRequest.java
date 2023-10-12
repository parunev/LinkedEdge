package com.parunev.linkededge.model.payload.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Request payload for updating multi-factor authentication (MFA) settings in user preferences.")
public class ProfileMfaRequest {

    @Schema(name = "Flag to indicate update of the MFA settings", example = "false", type = "Boolean")
    private boolean updateMfa;
}
