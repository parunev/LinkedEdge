package com.parunev.linkededge.controller;

import com.parunev.linkededge.model.payload.login.LoginRequest;
import com.parunev.linkededge.model.payload.login.LoginResponse;
import com.parunev.linkededge.model.payload.login.VerificationRequest;
import com.parunev.linkededge.model.payload.login.VerificationResponse;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
import com.parunev.linkededge.model.payload.registration.ResendTokenRequest;
import com.parunev.linkededge.service.AuthService;
import com.parunev.linkededge.util.LELogger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LELogger leLogger = new LELogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(
            @RequestBody RegistrationRequest request) {
        leLogger.info("Registration request received");
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @GetMapping("/register/confirm")
    public ResponseEntity<RegistrationResponse> confirmRegister(
            @RequestParam("token") String token){
        leLogger.info("Email confirmation request received");
        return new ResponseEntity<>(authService.confirmToken(token), HttpStatus.OK);
    }

    @PostMapping("/register/resend-token")
    public ResponseEntity<RegistrationResponse> resendConfirmationToken(
            @RequestBody ResendTokenRequest request) {
        leLogger.info("Resend token request received");
        return new ResponseEntity<>(authService.resendToken(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request){
        leLogger.info("Login request received");
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<LoginResponse> verifyLogin(
            @RequestBody VerificationRequest request) {
        leLogger.info("Login verification request received");
        return new ResponseEntity<>(authService.verifyLogin(request), HttpStatus.OK);
    }

    @PostMapping("/login/send-code")
    public ResponseEntity<VerificationResponse> sendCode(
            @RequestBody VerificationRequest verificationRequest){
        return new ResponseEntity<>(authService.sendVerificationCode(verificationRequest), HttpStatus.OK);
    }
}
