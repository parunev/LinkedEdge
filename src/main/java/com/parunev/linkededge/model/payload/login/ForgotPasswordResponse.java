package com.parunev.linkededge.model.payload.login;

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
public class ForgotPasswordResponse extends BasePayload {
    private String email;
}
