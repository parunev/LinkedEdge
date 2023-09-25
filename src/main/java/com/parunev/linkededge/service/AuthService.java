package com.parunev.linkededge.service;

import com.parunev.linkededge.model.ConfirmationToken;
import com.parunev.linkededge.model.JwtToken;
import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.User;
import com.parunev.linkededge.model.enums.Authority;
import com.parunev.linkededge.model.enums.TokenType;
import com.parunev.linkededge.model.payload.login.LoginRequest;
import com.parunev.linkededge.model.payload.login.LoginResponse;
import com.parunev.linkededge.model.payload.login.VerificationRequest;
import com.parunev.linkededge.model.payload.login.VerificationResponse;
import com.parunev.linkededge.model.payload.registration.RegistrationRequest;
import com.parunev.linkededge.model.payload.registration.RegistrationResponse;
import com.parunev.linkededge.model.payload.registration.ResendTokenRequest;
import com.parunev.linkededge.repository.ConfirmationTokenRepository;
import com.parunev.linkededge.repository.JwtTokenRepository;
import com.parunev.linkededge.repository.ProfileRepository;
import com.parunev.linkededge.repository.UserRepository;
import com.parunev.linkededge.security.exceptions.OTPValidationException;
import com.parunev.linkededge.security.exceptions.RegistrationFailedException;
import com.parunev.linkededge.security.exceptions.UserNotFoundException;
import com.parunev.linkededge.security.jwt.JwtService;
import com.parunev.linkededge.security.mfa.Email2FA;
import com.parunev.linkededge.security.mfa.Google2FA;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import com.parunev.linkededge.util.email.EmailSender;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
import static com.parunev.linkededge.util.email.EmailPatterns.confirmationEmail;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Google2FA google2FA;
    private final Email2FA email2FA;
    private final EmailSender emailSender;
    private final LELogger leLogger = new LELogger(AuthService.class);

    public final static String CONFIRMATION_LINK = "http://localhost:8080/edge-api/v1/auth/register/confirm?token=";

    public RegistrationResponse register(@Valid RegistrationRequest request){
        leLogger.info("Proceeding the registration request for user: {}", request.getUsername());
        userExists(request.getEmail(), request.getUsername());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .authorities(Collections.singleton(Authority.AUTHORITY_USER))
                .mfaEnabled(false)
                .mfaSecret(google2FA.generateNewSecret())
                .build();
        userRepository.save(user);
        leLogger.info("User saved to database: {}", user.getUsername());

        Profile profile = Profile.builder()
                .fullName(user.getFullName())
                .user(user)
                .build();
        profileRepository.save(profile);
        leLogger.info("User profile saved to database: {}", user.getUsername());

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
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided email not found: {}", request.getEmail());
                    throw new UserNotFoundException(
                            buildError("User with the provided email not found. Please ensure you have created an account", HttpStatus.NOT_FOUND)
                            );
                }
        );

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
                            buildError("Token not found. Please ensure you have the correct token or request a new one.", HttpStatus.UNPROCESSABLE_ENTITY)
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

        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("Your email was confirmed successfully. You can now login.")
                .email(confirmationToken.getUser().getUsername())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public LoginResponse login(@Valid LoginRequest request){
        User user = findUser(request.getUsername());

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

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAndSaveTokens(user);

        JwtToken token = JwtToken.builder()
                .user(user)
                .tokenValue(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        jwtTokenRepository.save(token);
        leLogger.info("Tokens revoked and new token saved for user: {}", user.getUsername());

        return LoginResponse.builder()
                .path(getCurrentRequest())
                .message("Login successful. Welcome, " + user.getUsername() + "!")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public VerificationResponse sendVerificationCode(VerificationRequest request) {
        User user = findUser(request.getUsername());

        email2FA.sendOtp(user, "Generic Message: LinkedEdge 2FA");
        return VerificationResponse.builder()
                .path(getCurrentRequest())
                .message("Verification code sent successfully. Please check your email for the verification code.")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    public LoginResponse verifyLogin(VerificationRequest request) {
        User user = findUser(request.getUsername());

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

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAndSaveTokens(user);

        JwtToken token = JwtToken.builder()
                .user(user)
                .tokenValue(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        jwtTokenRepository.save(token);

        return LoginResponse.builder()
                .path(getCurrentRequest())
                .message("Login successful. Welcome, " + user.getUsername() + "!")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .timestamp(LocalDateTime.now())
                .mfaEnabled(true)
                .status(HttpStatus.OK)
                .build();
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

    private User findUser(String username){
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
