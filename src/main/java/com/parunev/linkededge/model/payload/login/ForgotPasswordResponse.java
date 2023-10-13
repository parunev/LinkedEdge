package com.parunev.linkededge.model.payload.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload for or resetting a forgotten password", description = "Extends the BasePayload class" +
        "which means fields like 'path', 'message', 'status', 'timestamp' are also included in this response")
public class ForgotPasswordResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "User's email address", example = "linked_edge@gmail.com", description = "It's not necessary to include the email" +
            "inside the response but it does help the front-end to consume the information more precise")
    private String email;
}
