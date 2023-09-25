package com.parunev.linkededge.model.payload.login;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class VerificationResponse extends BasePayload {
}
