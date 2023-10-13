package com.parunev.linkededge.model.payload.profile.password;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload for user change password operation.", description = "Extends the BasePayload class" +
        "which means fields like 'path', 'message', 'status', 'timestamp' are also included in this response.")
public class ProfileChangePasswordResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "User's email address", example = "linked_edge@gmail.com", description = "It's not necessary to include the email" +
            "inside the response but it does help the front-end to consume the information more precise")
    private String email;
}
