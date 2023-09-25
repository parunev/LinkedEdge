package com.parunev.linkededge.security.payload;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ConstraintError extends BasePayload {
    private Map<String, String> errors;
}
