package com.parunev.linkededge.model.payload.profile.email;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ProfileEmailResponse extends BasePayload {
    private String newEmail;
    private boolean isEnabled;
}
