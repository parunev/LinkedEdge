package com.parunev.linkededge.service;

import com.nimbusds.jose.util.Pair;
import com.parunev.linkededge.model.*;
import com.parunev.linkededge.model.enums.Authority;
import com.parunev.linkededge.model.enums.TokenType;
import com.parunev.linkededge.model.payload.login.*;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
import com.parunev.linkededge.model.payload.registration.ResendTokenRequest;
import com.parunev.linkededge.repository.*;
import com.parunev.linkededge.security.exceptions.*;
import com.parunev.linkededge.security.jwt.JwtService;
import com.parunev.linkededge.security.mfa.Email2FA;
import com.parunev.linkededge.security.mfa.Google2FA;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.service.extraction.ExtractionService;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.email.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
import static com.parunev.linkededge.util.email.EmailPatterns.confirmationEmail;
import static com.parunev.linkededge.util.email.EmailPatterns.forgotPasswordEmail;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ExtractionService extractionService;
    private final Google2FA google2FA;
    private final Email2FA email2FA;
    private final EmailSender emailSender;
    private final LELogger leLogger = new LELogger(AuthService.class);

    public final static String CONFIRMATION_LINK = "http://localhost:8080/edge-api/v1/auth/register/confirm?token=";
    public final static String RESET_PASSWORD_LINK = "http://localhost:8080/edge-api/v1/auth/login/reset-password?token=";

    public RegistrationResponse register(@Valid RegistrationRequest request) {
        leLogger.info("Proceeding the registration request for user: {}", request.getUsername());
        userExists(request.getEmail(), request.getUsername());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .linkedInProfile(request.getProfileLink())
                .authority(Authority.AUTHORITY_USER)
                .mfaEnabled(false)
                .mfaSecret(google2FA.generateNewSecret())
                .build();
        userRepository.save(user);
        leLogger.info("User saved to database: {}", user.getUsername());

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        confirmationTokenRepository.save(confirmationToken);
        leLogger.info("Confirmation token saved to database: {} {}", confirmationToken.getTokenValue(),
                confirmationToken.getTokenType());

        emailSender.send(user.getEmail(), confirmationEmail(user.getFullName(), CONFIRMATION_LINK + confirmationToken.getTokenValue()),
                "Welcome to LinkedEdge! Verify your email to get started!");
        leLogger.info("Confirmation email has been sent to the user: {} {}", user.getUsername(), user.getEmail());

        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("Your registration was completed successfully. Please confirm your email account.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
    }

    public RegistrationResponse resendToken(@Valid ResendTokenRequest request){
        User user = findUserByEmail(request.getEmail());

        if (user.isEnabled()){
            throw new UserAlreadyEnabledException(
                    buildError("Your account is already enabled." +
                            "You can log in now.", HttpStatus.BAD_REQUEST));
        }

        List<ConfirmationToken> userConfirmationToken =
                confirmationTokenRepository.findAllByUserEmail(user.getEmail());

        confirmationTokenRepository.deleteAll(userConfirmationToken);
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        confirmationTokenRepository.save(confirmationToken);

        // Needs to be front-end link
        emailSender.send(user.getEmail(), confirmationEmail(user.getFullName(), CONFIRMATION_LINK + confirmationToken.getTokenValue()),
                "Welcome to LinkedEdge! Verify your email to get started!");

        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("A new confirmation email has been sent to your email address.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public RegistrationResponse confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByTokenValue(token)
                .orElseThrow(() -> {
                    leLogger.warn("Token not found");
                    throw new RegistrationFailedException(
                            buildError("Token not found. Please ensure you have the correct token or request a new one.", HttpStatus.NOT_FOUND)
                            );
                });

        isValidToken(confirmationToken);

        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

        if (confirmationToken.getUser().isEnabled()){
            leLogger.warn("User already enabled: {}", confirmationToken.getUser().getEmail());
            throw new RegistrationFailedException(
                    buildError("The user associated with this token is already enabled", HttpStatus.BAD_REQUEST)
            );
        }

        userRepository.enableAppUser(confirmationToken.getUser().getEmail());

        leLogger.info("User enabled: {}", confirmationToken.getUser().getEmail());


        try {
            extractionService.createProfile(confirmationToken.getUser());
            leLogger.info("User profile saved to database: {}", confirmationToken.getUser().getUsername());
        } catch (JSONException | HttpStatusCodeException e){
            leLogger.error("Something went wrong with the profile creation {} [}", e, e.getMessage());
            throw new InvalidExtractException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }


        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("Your email was confirmed successfully. You can now login.")
                .email(confirmationToken.getUser().getUsername())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public LoginResponse login(@Valid LoginRequest request){
        User user = findUserByUsername(request.getUsername());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        if (user.isMfaEnabled()){
            if (user.getMfaSecret() == null){
                user.setMfaSecret(google2FA.generateNewSecret());
                userRepository.save(user);
            }

            return LoginResponse.builder()
                    .path(getCurrentRequest())
                    .message("Please scan the QR code with Google Authenticator app to login.")
                    .secretImageUri(google2FA.generateQrCodeImageUri(user.getMfaSecret()))
                    .mfaEnabled(true)
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.OK)
                    .build();
        }

        revokeAndSaveTokens(user);
        Pair<String, String> tokens = generateJwtTokens(user);

        return LoginResponse.builder()
                .path(getCurrentRequest())
                .message("Login successful. Welcome, " + user.getUsername() + "!")
                .accessToken(tokens.getLeft())
                .refreshToken(tokens.getRight())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public VerificationResponse sendVerificationCode(VerificationRequest request) {
        User user = findUserByUsername(request.getUsername());

        isMfaEnabled(user.isMfaEnabled());

        email2FA.sendOtp(user, "Generic Message: LinkedEdge 2FA");
        return VerificationResponse.builder()
                .path(getCurrentRequest())
                .message("Verification code sent successfully. Please check your email for the verification code.")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public LoginResponse verifyLogin(VerificationRequest request) {
        User user = findUserByUsername(request.getUsername());

        isMfaEnabled(user.isMfaEnabled());

        boolean isGoogle2FAValid = google2FA.isOtpValid(user.getMfaSecret(), request.getCode());
        if (isGoogle2FAValid){
            leLogger.warn("Valid Google OTP");
        } else if (email2FA.verifyOtp(request)) {
            leLogger.info("Valid Email OTP");
        } else {
            leLogger.warn("Invalid Email OTP");
            throw new OTPValidationException(
                    buildError("Invalid OTP. Please ensure you have entered the correct verification code", HttpStatus.BAD_REQUEST)
            );
        }

        revokeAndSaveTokens(user);
        Pair<String, String> tokens = generateJwtTokens(user);

        return LoginResponse.builder()
                .path(getCurrentRequest())
                .message("Login successful. Welcome, " + user.getUsername() + "!")
                .accessToken(tokens.getLeft())
                .refreshToken(tokens.getRight())
                .timestamp(LocalDateTime.now())
                .mfaEnabled(true)
                .status(HttpStatus.OK)
                .build();
    }

    public ForgotPasswordResponse sendForgotPasswordEmail(@Valid ForgotPasswordRequest request){
        User user = findUserByEmail(request.getEmail());

        PasswordToken passwordToken = PasswordToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.PASSWORD)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        passwordTokenRepository.save(passwordToken);

        // Needs to be front-end link
        emailSender.send(user.getEmail(), forgotPasswordEmail(user.getFullName()
                        , RESET_PASSWORD_LINK + passwordToken.getTokenValue()),
                "LinkedEdge: Reset your password");

        leLogger.info("Forgot password email has been sent");
        return ForgotPasswordResponse.builder()
                .path(getCurrentRequest())
                .message("An email has been sent to your registered email address." +
                        " The password reset link will expire in 24 hours for security reasons.")
                .email(request.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public ForgotPasswordResponse resetPassword(String token, @Valid ResetPasswordRequest request){

        PasswordToken passwordToken = verifyPasswordToken(token);

        User user = passwordToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        leLogger.info("Password changed successfully");
        return ForgotPasswordResponse.builder()
                .path(getCurrentRequest())
                .message("Your password has been successfully reset." +
                        " You can now use your new password to log in.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public Pair<String, String> generateJwtTokens(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        JwtToken token = JwtToken.builder()
                .user(user)
                .tokenValue(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        jwtTokenRepository.save(token);
        leLogger.info("Tokens saved for user: {}", user.getUsername());

        return Pair.of(accessToken, refreshToken);
    }

    private void isMfaEnabled(boolean enabled) {
        if (!enabled){
            throw new UserMfaNotEnabledException(
                    buildError("Multi-factor authentication is not enabled for your account.", HttpStatus.BAD_REQUEST));
        }
    }

    public LoginResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            leLogger.warn("No authorization token found in the request");
            throw new AuthorizationNotFoundException(buildError("No authorization token found in the request", HttpStatus.UNAUTHORIZED));
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractEmail(refreshToken);

        JwtToken token = null;
        if (userEmail != null) {
            User user = findUserByEmail(userEmail);

            if (jwtService.isTokenValid(refreshToken, user)) {
                leLogger.debug("Refreshing token for user: {}", user.getEmail());
                String accessToken = jwtService.generateToken(user);
                revokeAndSaveTokens(user);
                token = JwtToken.builder()
                        .user(user)
                        .tokenValue(accessToken)
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .build();
                jwtTokenRepository.save(token);
                leLogger.info("Token refreshed successfully for user: {}", user.getEmail());
            } else {
                leLogger.warn("Invalid refresh token for user: {}", user.getEmail());
            }
        }

        if (token == null){
            leLogger.warn("Refresh token was not created.");
            throw new AuthorizationNotFoundException(buildError("Refresh token was not created. Pleas try again!", HttpStatus.UNAUTHORIZED));
        }

        return LoginResponse.builder()
                .path(getCurrentRequest())
                .accessToken(token.getTokenValue())
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private PasswordToken verifyPasswordToken(String token){
        return Optional.ofNullable(passwordTokenRepository.findByTokenValue(token))
                .flatMap(byToken -> byToken
                        .filter(
                                reset ->
                                        reset.getConfirmed() == null
                                                && reset
                                                .getExpires()
                                                .isAfter(LocalDateTime.now())))
                .orElseThrow(
                        () -> {
                            leLogger.warn("Token not found or is already used!");
                            throw new InvalidPasswordResetException(
                                    buildError("Token not found or is already used!", HttpStatus.NOT_FOUND));
                        });
    }


    private void revokeAndSaveTokens(User user) {
        leLogger.info("Revoking and saving tokens for user: {}", user.getUsername());

        List<JwtToken> validTokens = jwtTokenRepository.findAllValidTokenByUserId(user.getId());
        if (validTokens.isEmpty()){
            leLogger.info("No valid tokens found to revoke");
            return;
        }

        validTokens.forEach(jwtToken -> {
            jwtToken.setExpired(true);
            jwtToken.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validTokens);
    }

    private void isValidToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmed() != null) {
            leLogger.warn("Token already confirmed: {}", confirmationToken.getParameters());
            throw new RegistrationFailedException(
                    buildError("The provided token has already been confirmed", HttpStatus.BAD_REQUEST));
        }

        if (confirmationToken.getExpires().isBefore(LocalDateTime.now())) {
            leLogger.warn("Token has expired: {}", confirmationToken.getParameters());
            throw new RegistrationFailedException(
                    buildError("The provided token has expired. Please request a new one", HttpStatus.BAD_REQUEST)
            );
        }
    }


    private void userExists(String email, String username) {
        boolean emailExists = userRepository.existsByEmail(email);
        boolean usernameExists = userRepository.existsByUsername(username);

        String message;
        if (emailExists && usernameExists){
            message = "Email and username already exist. Please try different ones.";
        } else if (emailExists) {
            message = "Email already exists. Please try another one.";
        } else if (usernameExists) {
            message = "Username already exists. Please try another one.";
        } else {
            message = "true";
        }

        if(!message.equals("true")){
            throw new RegistrationFailedException(buildError(message, HttpStatus.BAD_REQUEST));
        }
    }

    private ApiError buildError(String message, HttpStatus status){
        return ApiError.builder()
                .path(getCurrentRequest())
                .error(message)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided email not found: {}", email);
                    throw new UserNotFoundException(
                            buildError("User with the provided email not found. Please ensure you have created an account", HttpStatus.NOT_FOUND)
                    );
                }
        );
    }

    private User findUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided username not found: {}",username);
                    throw new UserNotFoundException(
                            buildError("User with the provided username not found. Please ensure you have entered the correct username", HttpStatus.NOT_FOUND)
                    );
                }
        );
    }
}
