package com.parunev.linkededge.model.payload.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
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
public class LoginResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String secretImageUri;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean mfaEnabled;
}
