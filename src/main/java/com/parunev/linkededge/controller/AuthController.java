package com.parunev.linkededge.controller;

import com.parunev.linkededge.model.payload.login.*;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
import com.parunev.linkededge.model.payload.registration.ResendTokenRequest;
import com.parunev.linkededge.service.AuthService;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.annotations.openapi.auth.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/auth")
@Tag(name = "Authentication Controller", description = "API endpoints for user registration, login, and related operations")
public class AuthController {

    private final AuthService authService;
    private final LELogger leLogger = new LELogger(AuthController.class);

    @ApiRegisterUser
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for user registration")
            @RequestBody RegistrationRequest request) {
        leLogger.info("Registration request received");
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @ApiConfirmRegistration
    @GetMapping("/register/confirm")
    public ResponseEntity<RegistrationResponse> confirmRegister(
            @Parameter(in = ParameterIn.QUERY, name = "token", description = "Email confirmation token") @RequestParam("token") String token){
        leLogger.info("Email confirmation request received");
        return new ResponseEntity<>(authService.confirmToken(token), HttpStatus.OK);
    }

    @ApiResendToken
    @PostMapping("/register/resend-token")
    public ResponseEntity<RegistrationResponse> resendConfirmationToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for resending a confirmation token")
            @RequestBody ResendTokenRequest request) {
        leLogger.info("Resend token request received");
        return new ResponseEntity<>(authService.resendToken(request), HttpStatus.OK);
    }

    @ApiLogin
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for user login.")
            @RequestBody LoginRequest request){
        leLogger.info("Login request received");
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @ApiVerifyLogin
    @PostMapping("/login/verify")
    public ResponseEntity<LoginResponse> verifyLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for user verification")
            @RequestBody VerificationRequest request) {
        leLogger.info("Login verification request received");
        return new ResponseEntity<>(authService.verifyLogin(request), HttpStatus.OK);
    }

    @ApiSendCode
    @PostMapping("/login/send-code")
    public ResponseEntity<VerificationResponse> sendCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for user verification")
            @RequestBody VerificationRequest verificationRequest){
        leLogger.info("2FA Code request received");
        return new ResponseEntity<>(authService.sendVerificationCode(verificationRequest), HttpStatus.OK);
    }

    @ApiForgotPassword
    @PostMapping("/login/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for resetting a forgotten password")
            @RequestBody ForgotPasswordRequest request) {
        leLogger.info("Forgot password request received");
        return new ResponseEntity<>(authService.sendForgotPasswordEmail(request), HttpStatus.OK);
    }

    @ApiResetPassword
    @PostMapping("/login/reset-password")
    public ResponseEntity<ForgotPasswordResponse> resetPassword(
            @Parameter(in = ParameterIn.QUERY, name = "token", description = "Password confirmation token")@RequestParam("token") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Request payload for resetting the user's password.")
            @RequestBody ResetPasswordRequest request) {
        leLogger.info("Reset password request received");
        return new ResponseEntity<>(authService.resetPassword(token, request), HttpStatus.OK);
    }

    @ApiRefreshToken
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request){
        leLogger.info("Request to reset the access token");
        return new ResponseEntity<>(authService.refreshToken(request), HttpStatus.OK);
    }
}
