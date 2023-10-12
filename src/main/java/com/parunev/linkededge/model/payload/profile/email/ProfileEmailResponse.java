package com.parunev.linkededge.model.payload.profile.email;

import com.parunev.linkededge.model.commons.BasePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload after successfully changing the user's email address in the profile.",
        description = "Extends the BasePayload class" +
                "which means fields like 'path', 'message', 'status', 'timestamp' are also included in this response.")
public class ProfileEmailResponse extends BasePayload {

    @Schema(name = "New email address", example = "linked_edge_new@gmail.com", type = "String")
    private String newEmail;

    @Schema(name = "Indicates whether the account is enabled", example = "false", type = "Boolean")
    private boolean isEnabled;
}
