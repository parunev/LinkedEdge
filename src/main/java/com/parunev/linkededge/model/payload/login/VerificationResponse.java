package com.parunev.linkededge.model.payload.login;

import com.parunev.linkededge.model.commons.BasePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Response payload for user verification in the multi-factor authentication process.",
        description = "Extends the BasePayload class" +
                "which means fields like 'path', 'message', 'status', 'timestamp' are included in this response." +
                "Furthermore it's left empty. Other developers might want to include information inside the response," +
                "so their front-end team could understand better what's going on.")
public class VerificationResponse extends BasePayload {

}
