package com.parunev.linkededge.security.payload;

import com.parunev.linkededge.model.commons.BasePayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ApiResponse extends BasePayload {
}
