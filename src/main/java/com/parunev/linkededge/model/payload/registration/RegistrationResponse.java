package com.parunev.linkededge.model.payload.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.parunev.linkededge.model.commons.BasePayload;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegistrationResponse extends BasePayload {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

}
