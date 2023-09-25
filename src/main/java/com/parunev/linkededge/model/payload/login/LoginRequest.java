package com.parunev.linkededge.model.payload.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Please enter your username")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{6,29}$")
    private String username;

    @NotBlank(message = "Please enter your password.")
    private String password;
}
