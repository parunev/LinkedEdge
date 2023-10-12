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

import static com.parunev.linkededge.util.ConfirmationTokenUtil.isValidToken;
import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;
import static com.parunev.linkededge.util.email.EmailPatterns.confirmationEmail;
import static com.parunev.linkededge.util.email.EmailPatterns.forgotPasswordEmail;

/**
 * The `AuthService` class is responsible for user authentication, registration, password reset,
 * and related security features for the LinkedEdge application. It provides a range of services,
 * including user registration, confirmation, login, and multi-factor authentication.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */

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

    // Constants for confirmation and password reset links
    public final static String CONFIRMATION_LINK = "http://localhost:8080/edge-api/v1/auth/register/confirm?token=";
    public final static String RESET_PASSWORD_LINK = "http://localhost:8080/edge-api/v1/auth/login/reset-password?token=";

    /**
     * Registers a new user in the LinkedEdge application. This method is responsible for creating a new user account
     * and sending a confirmation email for email verification. It performs the following steps:
     * <p>
     * 1. Validates the provided registration request, ensuring that the email and username are unique.
     * <p>
     * 2. Creates a new User entity with the provided user details, including username, email, and password.
     * <p>
     * 3. Generates a confirmation token and associates it with the user for email verification.
     * <p>
     * 4. Sends a confirmation email to the user containing a verification link with the confirmation token.
     *
     * @param request The registration request containing user information.
     * @return A `RegistrationResponse` containing details of the registration status, email, timestamp, and HTTP status.
     * @throws AuthServiceException if registration fails or if the provided email or username already exist.
     */
    public RegistrationResponse register(@Valid RegistrationRequest request) {
        // Step 1: Validation and User Existence Check
        // Validate if the provided email and username are unique, ensuring no duplicates.
        leLogger.info("Proceeding the registration request for user: {}", request.getUsername());
        userExists(request.getEmail(), request.getUsername());

        // Step 2: User Entity Creation
        // Create a new User entity with the provided user details in the request.
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

        // Step 3: Confirmation Token Generation
        // Generate a confirmation token and associate it with the user for email verification.
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        confirmationTokenRepository.save(confirmationToken);
        leLogger.info("Confirmation token saved to database: {} {}", confirmationToken.getTokenValue(),
                confirmationToken.getTokenType());

        // Step 4: Sending Confirmation Email
        // Send a confirmation email to the user's email address with a verification link.
        emailSender.send(user.getEmail(), confirmationEmail(user.getFullName(), CONFIRMATION_LINK + confirmationToken.getTokenValue()),
                "Welcome to LinkedEdge! Verify your email to get started!");
        leLogger.info("Confirmation email has been sent to the user: {} {}", user.getUsername(), user.getEmail());

        // Construct and return a RegistrationResponse with relevant details.
        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("Your registration was completed successfully. Please confirm your email account.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Resends a confirmation token to the user's email address for email verification. This method is used when a user's
     * account is not yet enabled. It performs the following steps:
     * <p>
     * 1. Finds the user by their email address.
     * <p>
     * 2. Checks if the user's account is already enabled. If enabled, it throws an exception indicating that the user can log in.
     * <p>
     * 3. Deletes any existing confirmation tokens associated with the user's email.
     * <p>
     * 4. Generates a new confirmation token for email verification.
     * <p>
     * 5. Sends a new confirmation email to the user's email address.
     *
     * @param request The resend token request with the user's email.
     * @return A `RegistrationResponse` indicating the status of the token resend operation.
     * @throws AuthServiceException if the user's account is already enabled or if there is an issue with the token generation.
     */
    public RegistrationResponse resendToken(@Valid ResendTokenRequest request){
        // Step 1: Find User by Email
        // Retrieve the user from the database using their email address.
        User user = findUserByEmail(request.getEmail());

        // Step 2: Check User's Account Status
        // Check if the user's account is already enabled. If enabled, they should log in.
        if (user.isEnabled()){
            throw new AuthServiceException(
                    buildError("Your account is already enabled." +
                            "You can log in now.", HttpStatus.BAD_REQUEST));
        }

        // Step 3: Delete Existing Confirmation Tokens
        // Delete any existing confirmation tokens associated with the user's email.
        List<ConfirmationToken> userConfirmationToken =
                confirmationTokenRepository.findAllByUserEmail(user.getEmail());
        confirmationTokenRepository.deleteAll(userConfirmationToken);

        // Step 4: Generate New Confirmation Token
        // Generate a new confirmation token and associate it with the user for email verification.
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        confirmationTokenRepository.save(confirmationToken);

        // Step 5: Send New Confirmation Email
        // Note: Needs to be a front-end link
        // Send a new confirmation email to the user's email address with the updated verification link.
        emailSender.send(user.getEmail(), confirmationEmail(user.getFullName(), CONFIRMATION_LINK + confirmationToken.getTokenValue()),
                "Welcome to LinkedEdge! Verify your email to get started!");

        // Construct and return a RegistrationResponse with relevant details.
        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("A new confirmation email has been sent to your email address.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Confirms a user's email by validating the provided confirmation token. This method is used to complete the email
     * verification process and enable the user's account. It performs the following steps:
     * <p>
     * 1. Retrieves the confirmation token from the database based on the token value.
     * <p>
     * 2. Validates the token to ensure it is not expired.
     * <p>
     * 3. Updates the confirmation timestamp for the token.
     * <p>
     * 4. Checks if the user associated with the token is already enabled; if so, an exception is thrown.
     * <p>
     * 5. Enables the user's account by marking it as enabled in the repository.
     * <p>
     * 6. Attempts to create the user's profile, and if successful, logs the action.
     *
     * @param token The confirmation token used for email verification.
     * @return A `RegistrationResponse` indicating the successful confirmation of the user's email.
     * @throws AuthServiceException if the token is not found, already used, or the associated user is already enabled.
     * @throws InvalidExtractException if there is an issue with user profile creation.
     */
    @Transactional
    public RegistrationResponse confirmToken(String token) {
        // Step 1: Retrieve Confirmation Token
        // Retrieve the confirmation token from the database based on the provided token value.
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByTokenValue(token)
                .orElseThrow(() -> {
                    leLogger.warn("Token not found");
                    throw new AuthServiceException(
                            buildError("Token not found. Please ensure you have the correct token or request a new one.", HttpStatus.NOT_FOUND)
                            );
                });

        // Step 2: Validate the Confirmation Token
        // Validate the token to ensure it is not expired.
        isValidToken(confirmationToken);

        // Step 3: Update Confirmation Timestamp
        // Update the confirmation timestamp for the token.
        confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

        // Step 4: Check User Account Status
        // Check if the user associated with the token is already enabled. If enabled, an exception is thrown.
        if (confirmationToken.getUser().isEnabled()){
            leLogger.warn("User already enabled: {}", confirmationToken.getUser().getEmail());
            throw new AuthServiceException(
                    buildError("The user associated with this token is already enabled", HttpStatus.BAD_REQUEST)
            );
        }

        // Step 5: Enable User's Account
        // Enable the user's account by marking it as enabled in the database.
        userRepository.enableAppUser(confirmationToken.getUser().getEmail());
        leLogger.info("User enabled: {}", confirmationToken.getUser().getEmail());

        // Step 6: Create User's Profile
        // Attempt to create the user's profile. If successful, log the action.
        try {
            // Scrapping the information from the LinkedIn account
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

        // Construct and return a RegistrationResponse with relevant details.
        return RegistrationResponse.builder()
                .path(getCurrentRequest())
                .message("Your email was confirmed successfully. You can now login.")
                .email(confirmationToken.getUser().getUsername())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Handles user login by validating the provided credentials and generating access tokens. This method supports
     * two-factor authentication (2FA) if enabled for the user. It performs the following steps:
     * <p>
     * 1. Finds the user by their username.
     * <p>
     * 2. Authenticates the user by validating the provided username and password using the Authentication Manager.
     * <p>
     * 3. If 2FA is enabled for the user:
     * <p>
     *    a. Checks if the user's 2FA secret is available; if not, generates a new one and saves it.
     * <p>
     *    b. Returns a response instructing the user to scan a QR code with the Google Authenticator app for 2FA setup.
     * <p>
     * 4. If 2FA is not enabled, revokes and saves any existing tokens, and generates new access tokens for the user.
     *
     * @param request The login request containing user credentials.
     * @return A `LoginResponse` indicating the status of the login, including access tokens and 2FA setup instructions.
     * @throws AuthServiceException if the provided credentials are invalid or if there is an issue with 2FA setup.
     */
    public LoginResponse login(@Valid LoginRequest request){
        // Step 1: Find User by Username
        // Retrieve the user from the database using their username.
        User user = findUserByUsername(request.getUsername());

        // Step 2: Authenticate User
        // Authenticate the user by validating the provided username and password using the Authentication Manager.
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Step 3: Handle Two-Factor Authentication (2FA)
        if (user.isMfaEnabled()){
            if (user.getMfaSecret() == null){
                // Step 3a: Generate New 2FA Secret
                // If the user's 2FA secret is not available, generate a new one and save it.
                user.setMfaSecret(google2FA.generateNewSecret());
                userRepository.save(user);
            }

            // Step 3b: Return 2FA Setup Instructions
            // Return a response instructing the user to scan a QR code with the Google Authenticator app for 2FA setup.
            return LoginResponse.builder()
                    .path(getCurrentRequest())
                    .message("Please scan the QR code with Google Authenticator app to login.")
                    .secretImageUri(google2FA.generateQrCodeImageUri(user.getMfaSecret()))
                    .mfaEnabled(true)
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.OK)
                    .build();
        }

        // Step 4: Handle Regular Login
        // If 2FA is not enabled, revoke and save any existing tokens, and generate new access tokens for the user.
        revokeAndSaveTokens(user);
        Pair<String, String> tokens = generateJwtTokens(user);

        // Construct and return a LoginResponse with relevant details.
        return LoginResponse.builder()
                .path(getCurrentRequest())
                .message("Login successful. Welcome, " + user.getUsername() + "!")
                .accessToken(tokens.getLeft())
                .refreshToken(tokens.getRight())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Sends a verification code to the user's email address for Multi-Factor Authentication (MFA). This method is used to
     * initiate the MFA login process. It performs the following steps:
     * <p>
     * 1. Finds the user by their username.
     * <p>
     * 2. Checks if MFA is enabled for the user; if not, an exception is thrown.
     * <p>
     * 3. Sends an OTP (One-Time Password) verification code to the user's email address.
     *
     * @param request The verification request containing the username for which MFA code needs to be sent.
     * @return A `VerificationResponse` indicating the status of the code sending process.
     * @throws AuthServiceException if MFA is not enabled for the user.
     */
    public VerificationResponse sendVerificationCode(VerificationRequest request) {
        // Step 1: Find User by Username
        // Retrieve the user from the database using their username.
        User user = findUserByUsername(request.getUsername());

        // Step 2: Check if MFA is Enabled
        // Check if Multi-Factor Authentication (MFA) is enabled for the user. If not, throw an exception.
        isMfaEnabled(user.isMfaEnabled());

        // Step 3: Send OTP Verification Code
        // Send an OTP (One-Time Password) verification code to the user's email address for MFA.
        email2FA.sendOtp(user, "Generic Message: LinkedEdge 2FA");

        // Construct and return a VerificationResponse with relevant details.
        return VerificationResponse.builder()
                .path(getCurrentRequest())
                .message("Verification code sent successfully. Please check your email for the verification code.")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Verifies a user's login by validating the Multi-Factor Authentication (MFA) code provided. This method is used after
     * the user receives a verification code and provides it for MFA login. It performs the following steps:
     * <p>
     * 1. Finds the user by their username.
     * <p>
     * 2. Checks if MFA is enabled for the user; if not, an exception is thrown.
     * <p>
     * 3. Validates the MFA code provided by the user. It first checks if it's a valid Google OTP (One-Time Password),
     *    and if not, it attempts to verify it as an email OTP.
     * <p>
     * 4. If the MFA code is valid, it revokes and saves any existing tokens, and generates new access tokens for the user.
     * <p>
     * 5. Constructs a `LoginResponse` to indicate a successful login with relevant details.
     *
     * @param request The verification request containing the username and MFA code for login.
     * @return A `LoginResponse` indicating the status of the MFA login and providing access tokens.
     * @throws OTPValidationException if the provided MFA code is invalid.
     */
    public LoginResponse verifyLogin(VerificationRequest request) {
        // Step 1: Find User by Username
        // Retrieve the user from the database using their username.
        User user = findUserByUsername(request.getUsername());

        // Step 2: Check if MFA is Enabled
        // Check if Multi-Factor Authentication (MFA) is enabled for the user. If not, throw an exception.
        isMfaEnabled(user.isMfaEnabled());

        // Step 3: Validate MFA Code
        // Validate the Multi-Factor Authentication (MFA) code provided by the user.
        boolean isGoogle2FAValid = google2FA.isOtpValid(user.getMfaSecret(), request.getCode());

        // Check if the code is a valid Google OTP. If not, attempt to verify it as an email OTP.
        if (isGoogle2FAValid){
            leLogger.warn("Valid Google OTP");
        } else if (email2FA.verifyOtp(request)) {
            leLogger.info("Valid Email OTP");
        } else {
            // Step 3 (Error Handling): Invalid MFA Code
            // If the provided MFA code is invalid, throw an OTPValidationException.
            leLogger.warn("Invalid Email OTP");
            throw new OTPValidationException(
                    buildError("Invalid OTP. Please ensure you have entered the correct verification code", HttpStatus.BAD_REQUEST)
            );
        }

        // Step 4: Handle Successful MFA Verification
        // If the MFA code is valid, revoke and save any existing tokens and generate new access tokens for the user.

        // Step 4a: Revoke and Save Tokens
        revokeAndSaveTokens(user);

        // Step 4b: Generate New Access Tokens
        Pair<String, String> tokens = generateJwtTokens(user);

        // Step 5: Construct and Return a LoginResponse
        // Construct and return a LoginResponse indicating a successful login with access tokens.
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

    /**
     * Initiates the process of sending a password reset email to the user's registered email address. This method is used
     * when a user forgets their password and requests a password reset. It performs the following steps:
     * <p>
     * 1. Finds the user by their email address.
     * <p>
     * 2. Generates a password reset token, sets its expiration, and associates it with the user.
     * <p>
     * 3. Sends an email containing a password reset link to the user's email address.
     * <p>
     * 4. Constructs and returns a `ForgotPasswordResponse` to inform the user about the email sent.
     *
     * @param request The forgot password request containing the user's email address.
     * @return A `ForgotPasswordResponse` indicating the status of the password reset email process.
     */
    public ForgotPasswordResponse sendForgotPasswordEmail(@Valid ForgotPasswordRequest request){
        // Step 1: Find User by Email
        // Retrieve the user from the database using their email address.
        User user = findUserByEmail(request.getEmail());

        // Step 2: Generate Password Reset Token
        // Generate a unique password reset token, set its expiration to 24 hours, and associate it with the user.
        PasswordToken passwordToken = PasswordToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.PASSWORD)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        passwordTokenRepository.save(passwordToken);

        // Step 3: Send Password Reset Email
        // Send email to the user's registered email address containing a password reset link.
        // Note: The link should point to a front-end page where the user can reset their password.
        emailSender.send(user.getEmail(), forgotPasswordEmail(user.getFullName()
                        , RESET_PASSWORD_LINK + passwordToken.getTokenValue()),
                "LinkedEdge: Reset your password");
        leLogger.info("Forgot password email has been sent");

        // Step 4: Construct and Return a ForgotPasswordResponse
        // Construct and return a response indicating that a password reset email has been sent.
        return ForgotPasswordResponse.builder()
                .path(getCurrentRequest())
                .message("An email has been sent to your registered email address." +
                        " The password reset link will expire in 24 hours for security reasons.")
                .email(request.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Resets the user's password after a successful verification of the password reset token. This method is used when a user
     * follows a password reset link, provides a new password, and requests to reset their password. It performs the following steps:
     * <p>
     * 1. Verifies the password reset token provided in the URL.
     * <p>
     * 2. Retrieves the user associated with the token.
     * <p>
     * 3. Updates the user's password with the new password provided in the request.
     * <p>
     * 4. Saves the updated user information in the database.
     * <p>
     * 5. Constructs and returns a `ForgotPasswordResponse` to inform the user about the successful password reset.
     *
     * @param token The password reset token provided in the URL for verification.
     * @param request The reset password request containing the new password.
     * @return A `ForgotPasswordResponse` indicating the status of the password reset process.
     * @throws AuthServiceException if the provided token is invalid or has expired.
     */
    public ForgotPasswordResponse resetPassword(String token, @Valid ResetPasswordRequest request){
        // Step 1: Verify Password Reset Token
        // Verify the password reset token provided in the URL to ensure its validity.
        PasswordToken passwordToken = verifyPasswordToken(token);

        // Step 2: Retrieve User Associated with Token
        // Retrieve the user associated with the verified password reset token.
        User user = passwordToken.getUser();

        // Step 3: Update User's Password
        // Update the user's password with the new password provided in the request. Ensure the password is securely hashed.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Step 4: Save Updated User Information
        // Save the updated user information with the new password in the database.
        userRepository.save(user);
        leLogger.info("Password changed successfully");

        // Step 5: Construct and Return a ForgotPasswordResponse
        // Construct and return a response indicating that the password has been successfully reset.
        return ForgotPasswordResponse.builder()
                .path(getCurrentRequest())
                .message("Your password has been successfully reset." +
                        " You can now use your new password to log in.")
                .email(user.getEmail())
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Generates a pair of JWT tokens (access token and refresh token) for a user. These tokens are essential for
     * user authentication and authorization. This method performs the following steps:
     * <p>
     * 1. Generates an access token for the user using the JwtService.
     * <p>
     * 2. Generates a refresh token for the user using the JwtService.
     * <p>
     * 3. Creates a new JwtToken object to represent the access token.
     * <p>
     * 4. Saves the JwtToken in the database to keep track of the token's state.
     * <p>
     * 5. Returns the pair of generated tokens (access token and refresh token).
     *
     * @param user The user for whom the tokens are generated.
     * @return A Pair of strings, where the first element is the access token and the second is the refresh token.
     */
    public Pair<String, String> generateJwtTokens(User user) {
        // Step 1: Generate Access Token
        // Generate an access token for the user using the JwtService.
        String accessToken = jwtService.generateToken(user);

        // Step 2: Generate Refresh Token
        // Generate a refresh token for the user using the JwtService.
        String refreshToken = jwtService.generateRefreshToken(user);

        // Step 3: Create JwtToken for Access Token
        // Create a new JwtToken object to represent the access token.
        JwtToken token = JwtToken.builder()
                .user(user)
                .tokenValue(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        // Step 4: Save JwtToken in the Database
        // Save the JwtToken in the database to keep track of the token's state.
        jwtTokenRepository.save(token);
        leLogger.info("Tokens saved for user: {}", user.getUsername());

        // Step 5: Return the Pair of Tokens
        // Return the pair of generated tokens (access token and refresh token).
        return Pair.of(accessToken, refreshToken);
    }

    /**
     * Checks if multi-factor authentication (MFA) is enabled for a user. This method is used to enforce MFA requirements
     * when certain actions or operations require MFA. It performs the following steps:
     * <p>
     * 1. Accepts a boolean parameter 'enabled' which represents the MFA status for a user.
     * <p>
     * 2. If MFA is not enabled (enabled=false), it throws an `AuthServiceException` to indicate that MFA is not enabled
     *    for the user and prevents further execution.
     *
     * @param enabled A boolean value representing whether MFA is enabled for the user.
     * @throws AuthServiceException if MFA is not enabled for the user, indicating a bad request.
     */
    private void isMfaEnabled(boolean enabled) {
        // Step 1: Accept a Boolean Parameter
        // Accept a boolean parameter 'enabled' which represents the MFA status for a user.

        // Step 2: Check MFA Status
        // If MFA is not enabled (enabled=false), throw an `AuthServiceException`.
        if (!enabled){
            throw new AuthServiceException(
                    buildError("Multi-factor authentication is not enabled for your account.", HttpStatus.BAD_REQUEST));
        }
    }

    /**
     * Refreshes the user's access token by generating a new token if the provided refresh token is valid.
     * This method is called when the user's current access token has expired, and they need a new one without
     * having to log in again.
     *
     * @param request The HTTP servlet request, which should contain an Authorization header with a valid refresh token.
     * @return A `LoginResponse` object containing the new access token and its metadata.
     * @throws AuthorizationNotFoundException if the provided refresh token is missing or invalid.
     */
    public LoginResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        // Step 1: Check Authorization Header
        // If the Authorization header is missing or doesn't start with "Bearer", raise an exception.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            leLogger.warn("No authorization token found in the request");
            throw new AuthorizationNotFoundException(buildError("No authorization token found in the request", HttpStatus.UNAUTHORIZED));
        }

        // Step 2: Extract Refresh Token
        // Extract the refresh token from the Authorization header.
        refreshToken = authHeader.substring(7); // Remove "Bearer " prefix.

        // Step 3: Extract User Email from Refresh Token
        // Extract the user's email from the refresh token using the `jwtService`.
        userEmail = jwtService.extractEmail(refreshToken);

        JwtToken token = null;
        if (userEmail != null) {

            // Step 4: Find User by Email
            // Retrieve the user from the database based on the extracted user email.
            User user = findUserByEmail(userEmail);

            // Step 5: Check Token Validity
            // Check if the provided refresh token is valid for the user.
            if (jwtService.isTokenValid(refreshToken, user)) {
                leLogger.debug("Refreshing token for user: {}", user.getEmail());

                // Step 6: Generate a New Access Token
                // Generate a new access token for the user.
                String accessToken = jwtService.generateToken(user);

                // Step 7: Revoke and Save Tokens
                // Revoke the old tokens and save the new access token.
                revokeAndSaveTokens(user);

                // Step 8: Create a New JwtToken Object
                // Create a new JwtToken object to represent the refreshed access token.
                token = JwtToken.builder()
                        .user(user)
                        .tokenValue(accessToken)
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .build();

                // Step 9: Save the Token
                // Save the new access token to the database.
                jwtTokenRepository.save(token);
                leLogger.info("Token refreshed successfully for user: {}", user.getEmail());
            } else {
                leLogger.warn("Invalid refresh token for user: {}", user.getEmail());
            }
        }

        // Step 10: Handle Token Creation Failure
        // If the `token` is still `null`, it means the refresh token was not created or is invalid.
        if (token == null){
            leLogger.warn("Refresh token was not created.");
            throw new AuthorizationNotFoundException(buildError("Refresh token was not created. Pleas try again!", HttpStatus.UNAUTHORIZED));
        }

        // Step 11: Return the New Access Token
        // Return a `LoginResponse` object containing the new access token and its metadata.
        return LoginResponse.builder()
                .path(getCurrentRequest())
                .accessToken(token.getTokenValue())
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Verifies the validity of a password reset token by checking its existence, expiration, and previous use.
     *
     * @param token The password reset token to be verified.
     * @return A `PasswordToken` object if the token is valid.
     * @throws AuthServiceException if the token is not found, expired, or has already been used.
     */
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
                            throw new AuthServiceException(
                                    buildError("Token not found or is already used!", HttpStatus.NOT_FOUND));
                        });
    }

    /**
     * Revokes and saves JWT tokens associated with a user. It marks valid tokens as expired and revoked in the repository.
     *
     * @param user The user for whom tokens need to be revoked.
     */
    private void revokeAndSaveTokens(User user) {
        leLogger.info("Revoking and saving tokens for user: {}", user.getUsername());

        // Retrieve all valid tokens associated with the user
        List<JwtToken> validTokens = jwtTokenRepository.findAllValidTokenByUserId(user.getId());

        // If no valid tokens are found, log a message and exit
        if (validTokens.isEmpty()){
            leLogger.info("No valid tokens found to revoke");
            return;
        }

        // Mark each valid token as expired and revoked
        validTokens.forEach(jwtToken -> {
            jwtToken.setExpired(true);
            jwtToken.setRevoked(true);
        });

        // Save the updated token information in the repository
        jwtTokenRepository.saveAll(validTokens);
    }

    /**
     * Check if a user with the provided email or username already exists in the system.
     * If a user with the same email or username exists, it throws an exception.
     *
     * @param email The email to check for existence.
     * @param username The username to check for existence.
     * @throws AuthServiceException if a user with the provided email or username already exists.
     */
    private void userExists(String email, String username) {
        // Check if a user with the provided email already exists
        boolean emailExists = userRepository.existsByEmail(email);

        // Check if a user with the provided username already exists
        boolean usernameExists = userRepository.existsByUsername(username);

        String message; // Initialize a message to store the result of the existence checks

        // Determine the message based on the existence checks
        if (emailExists && usernameExists){
            message = "Email and username already exist. Please try different ones.";
        } else if (emailExists) {
            message = "Email already exists. Please try another one.";
        } else if (usernameExists) {
            message = "Username already exists. Please try another one.";
        } else {
            message = "true"; // No conflicts found
        }

        // If the message indicates a conflict (not "true"), throw an exception
        if(!message.equals("true")){
            throw new AuthServiceException(buildError(message, HttpStatus.BAD_REQUEST));
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
                    throw new ResourceNotFoundException(
                            buildError("User with the provided email not found. Please ensure you have created an account", HttpStatus.NOT_FOUND)
                    );
                }
        );
    }

    private User findUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> {
                    leLogger.warn("User with the provided username not found: {}",username);
                    throw new ResourceNotFoundException(
                            buildError("User with the provided username not found. Please ensure you have entered the correct username", HttpStatus.NOT_FOUND)
                    );
                }
        );
    }
}
