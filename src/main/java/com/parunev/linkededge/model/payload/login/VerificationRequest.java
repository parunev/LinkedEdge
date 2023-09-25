package com.parunev.linkededge.model.payload.login;

import lombok.Data;

@Data
public class VerificationRequest {
    private String username;
    private String code;
}
