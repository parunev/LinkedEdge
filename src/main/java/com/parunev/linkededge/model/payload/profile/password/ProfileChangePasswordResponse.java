package com.parunev.linkededge.model.payload.profile.password;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ProfileChangePasswordResponse extends BasePayload {

    private String email;
}
