package com.parunev.linkededge.service;

import com.parunev.linkededge.model.ConfirmationToken;
import com.parunev.linkededge.model.PasswordToken;
import com.parunev.linkededge.model.Profile;
import com.parunev.linkededge.model.User;
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
import com.parunev.linkededge.util.email.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private JwtTokenRepository jwtTokenRepository;

    @Mock
    private PasswordTokenRepository passwordTokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private Google2FA google2FA;

    @Mock
    private Email2FA email2FA;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/edge-api/v1/auth")
                .buildRequest(new MockServletContext());

        ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);

        RequestContextHolder.setRequestAttributes(mockAttributes);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("username");
        registrationRequest.setEmail("email@gmail.com");
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("Test");
        registrationRequest.setPassword("password");
        registrationRequest.setConfirm("password");

        User savedUser = User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .password("encodedPassword")
                .mfaEnabled(false)
                .mfaSecret("secret")
                .build();

        when(google2FA.generateNewSecret()).thenReturn("secret");
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Profile savedProfile = Profile.builder()
                .fullName(savedUser.getFullName())
                .user(savedUser)
                .build();
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.CONFIRMATION)
                .expires(LocalDateTime.now().plusHours(24))
                .user(savedUser)
                .build();
        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);
        RegistrationResponse response = authService.register(registrationRequest);

        assertEquals("Your registration was completed successfully. Please confirm your email account.", response.getMessage());
        assertEquals(HttpStatus.CREATED, response.getStatus());

        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("username", capturedUser.getUsername());
        assertEquals("email@gmail.com", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
    }

    @ParameterizedTest
    @CsvSource({
            "true, true, Email and username already exist. Please try different ones.",
            "false, true, Email already exists. Please try another one.",
            "true, false, Username already exists. Please try another one."
    })
    void testRegister_UserExists(boolean usernameExists, boolean emailExists, String expectedErrorMessage) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("username");
        registrationRequest.setEmail("email@gmail.com");
        registrationRequest.setFirstName("Test");
        registrationRequest.setLastName("Test");
        registrationRequest.setPassword("password");
        registrationRequest.setConfirm("password");

        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(usernameExists);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(emailExists);

        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                authService.register(registrationRequest)
        );

        assertEquals(expectedErrorMessage, exception.getApiError().getError());
    }

    @Test
    void testResendToken() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setEnabled(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        List<ConfirmationToken> confirmationTokens = new ArrayList<>();
        when(confirmationTokenRepository.findAllByUserEmail("user@example.com")).thenReturn(confirmationTokens);

        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenAnswer(invocation -> invocation.<ConfirmationToken>getArgument(0));

        ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("user@example.com");
        RegistrationResponse response = authService.resendToken(request);

        verify(confirmationTokenRepository).deleteAll(confirmationTokens);
        verify(emailSender).send(eq("user@example.com"), anyString(), anyString());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("A new confirmation email has been sent to your email address.", response.getMessage());
        assertEquals("user@example.com", response.getEmail());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testResendToken_UserNotFound() {
        ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("user@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authService.resendToken(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getError().getStatus());
        assertEquals("User with the provided email not found. Please ensure you have created an account", exception.getError().getError());
        assertNotNull(exception.getError().getTimestamp());
        assertNotNull(exception.getError().getPath());
    }

    @Test
    void testResentToken_UserEnabled(){
        ResendTokenRequest request = new ResendTokenRequest();
        request.setEmail("user@example.com");

        User user = new User();
        user.setEmail("user@example.com");
        user.setEnabled(true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserAlreadyEnabledException exception = assertThrows(UserAlreadyEnabledException.class, () ->
                authService.resendToken(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getStatus());
        assertEquals("Your account is already enabled." +
                "You can log in now.", exception.getError().getError());
        assertNotNull(exception.getError().getTimestamp());
        assertNotNull(exception.getError().getPath());
    }

    @Test
    void testConfirmToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenValue("valid-token");
        confirmationToken.setExpires(LocalDateTime.now().plusHours(24));
        confirmationToken.setConfirmed(null);

        User user = new User();
        user.setEmail("user@example.com");
        user.setEnabled(false);
        confirmationToken.setUser(user);

        when(confirmationTokenRepository.findByTokenValue("valid-token")).thenReturn(Optional.of(confirmationToken));

        RegistrationResponse response = authService.confirmToken("valid-token");

        verify(userRepository).enableAppUser("user@example.com");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Your email was confirmed successfully. You can now login.", response.getMessage());
        assertEquals("user@example.com", confirmationToken.getUser().getEmail());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testConfirmToken_TokenAlreadyConfirmed() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenValue("valid-token");
        confirmationToken.setExpires(LocalDateTime.now().plusHours(24));
        confirmationToken.setConfirmed(LocalDateTime.now());

        User user = new User();
        user.setEmail("user@example.com");
        user.setEnabled(false);
        confirmationToken.setUser(user);

        when(confirmationTokenRepository.findByTokenValue("valid-token")).thenReturn(Optional.of(confirmationToken));

        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                authService.confirmToken("valid-token"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        assertEquals("The provided token has already been confirmed", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }

    @Test
    void testConfirmToken_TokenExpired() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenValue("valid-token");
        confirmationToken.setExpires(LocalDateTime.now().minusHours(24));

        User user = new User();
        user.setEmail("user@example.com");
        user.setEnabled(false);
        confirmationToken.setUser(user);

        when(confirmationTokenRepository.findByTokenValue("valid-token")).thenReturn(Optional.of(confirmationToken));

        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                authService.confirmToken("valid-token"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        assertEquals("The provided token has expired. Please request a new one", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }

    @Test
    void testConfirmToken_TokenNotFound() {
        when(confirmationTokenRepository.findByTokenValue("invalid-token")).thenReturn(Optional.empty());

        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                authService.confirmToken("invalid-token"));

        verify(confirmationTokenRepository, times(0)).updateConfirmedAt("token-value", LocalDateTime.now());
        verify(userRepository, times(0)).enableAppUser("some@email.com");

        assertEquals(HttpStatus.NOT_FOUND, exception.getApiError().getStatus());
        assertEquals("Token not found. Please ensure you have the correct token or request a new one.", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }

    @Test
    void testConfirmToken_UserAlreadyEnabled() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenValue("valid-token");
        confirmationToken.setExpires(LocalDateTime.now().plusHours(24));
        confirmationToken.setUser(new User());
        confirmationToken.getUser().setEnabled(true);

        when(confirmationTokenRepository.findByTokenValue("valid-token")).thenReturn(Optional.of(confirmationToken));

        RegistrationFailedException exception = assertThrows(RegistrationFailedException.class, () ->
                authService.confirmToken("valid-token"));

        verify(userRepository, times(0)).enableAppUser("some@email.com");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        assertEquals("The user associated with this token is already enabled", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }

    @Test
    void testLoginWithoutMFA() {
        LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password");

        User user = new User();
        user.setUsername("username");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.isMfaEnabled());
    }

    @Test
    void testLoginWithMFA() {
        LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password");

        User user = new User();
        user.setUsername("username");
        user.setMfaEnabled(true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(google2FA.generateNewSecret()).thenReturn("mocked-secret");
        when(google2FA.generateQrCodeImageUri("mocked-secret")).thenReturn("mocked-image-uri");

        LoginResponse response = authService.login(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.isMfaEnabled());
        assertNotNull(response.getSecretImageUri());
    }

    @Test
    void testLogin_UserDoNotExists() {
        LoginRequest request = new LoginRequest();
        request.setUsername("username");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authService.login(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getError().getStatus());
        assertEquals("User with the provided username not found. Please ensure you have entered the correct username", exception.getError().getError());
        assertNotNull(exception.getError().getTimestamp());
        assertNotNull(exception.getError().getPath());
    }

    @Test
    void testSendVerificationCode() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("some-code");
        User user = new User();
        user.setUsername("username");
        user.setMfaEnabled(true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        doNothing().when(email2FA).sendOtp(any(User.class), anyString());

        VerificationResponse response = authService.sendVerificationCode(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Verification code sent successfully. Please check your email for the verification code.", response.getMessage());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testSendVerificationCode_MfaDisabled() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("some-code");
        User user = new User();
        user.setUsername("username");
        user.setMfaEnabled(false);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        UserMfaNotEnabledException exception = assertThrows(UserMfaNotEnabledException.class, () ->
                authService.sendVerificationCode(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getStatus());
        assertEquals("Multi-factor authentication is not enabled for your account.", exception.getError().getError());
        assertNotNull(exception.getError().getTimestamp());
        assertNotNull(exception.getError().getPath());
    }

    @Test
    void testVerifyLoginWithValidGoogleOTP() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("google-otp");
        User user = new User();
        user.setUsername("username");
        user.setMfaSecret("google-secret");
        user.setMfaEnabled(true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(google2FA.isOtpValid("google-secret", "google-otp")).thenReturn(true);

        LoginResponse response = authService.verifyLogin(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Login successful. Welcome, username!", response.getMessage());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testVerifyLoginWithValidEmailOTP() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("google-otp");
        User user = new User();
        user.setUsername("username");
        user.setMfaSecret("google-secret");
        user.setMfaEnabled(true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(email2FA.verifyOtp(request)).thenReturn(true);

        LoginResponse response = authService.verifyLogin(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Login successful. Welcome, username!", response.getMessage());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testVerifyLoginWithInvalidOTP() {
        VerificationRequest request = new VerificationRequest();
        request.setUsername("username");
        request.setCode("google-otp");
        User user = new User();
        user.setUsername("username");
        user.setMfaEnabled(true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(google2FA.isOtpValid("google-secret", "invalid-otp")).thenReturn(false);
        when(email2FA.verifyOtp(request)).thenReturn(false);

        OTPValidationException exception = assertThrows(OTPValidationException.class,
                () -> authService.verifyLogin(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        assertEquals("Invalid OTP. Please ensure you have entered the correct verification code", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }

    @Test
    void testSendForgotPasswordEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@example.com");

        User user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("Test");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        PasswordToken passwordToken = PasswordToken.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenType(TokenType.PASSWORD)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        when(passwordTokenRepository.save(any(PasswordToken.class))).thenReturn(passwordToken);

        ForgotPasswordResponse response = authService.sendForgotPasswordEmail(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("An email has been sent to your registered email address. The password reset link will expire in 24 hours for security reasons.", response.getMessage());
        assertEquals("user@example.com", response.getEmail());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getPath());
    }

    @Test
    void testSendForgotPasswordEmail_UserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                authService.sendForgotPasswordEmail(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getError().getStatus());
        assertEquals("User with the provided email not found. Please ensure you have created an account", exception.getError().getError());
        assertNotNull(exception.getError().getTimestamp());
        assertNotNull(exception.getError().getPath());
    }

    @Test
    void testResetPassword() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("password");
        request.setConfirmPassword("password");

        User user = new User();
        user.setEmail("user@example.com");

        PasswordToken passwordToken = PasswordToken.builder()
                .tokenValue(token)
                .tokenType(TokenType.PASSWORD)
                .expires(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();

        when(passwordTokenRepository.findByTokenValue(token)).thenReturn(Optional.of(passwordToken));
        when(passwordEncoder.encode("new-password")).thenReturn("hashed-password");

        ForgotPasswordResponse response = authService.resetPassword(token, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Your password has been successfully reset. You can now use your new password to log in.", response.getMessage());
        assertEquals("user@example.com", response.getEmail());
    }

    @Test
    void testResetPassword_InvalidToken() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("password");
        request.setConfirmPassword("password");

        User user = new User();
        user.setEmail("user@example.com");

        when(passwordTokenRepository.findByTokenValue(token)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("new-password")).thenReturn("hashed-password");

        InvalidPasswordResetException exception = assertThrows(InvalidPasswordResetException.class,
                () -> authService.resetPassword(token, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getApiError().getStatus());
        assertEquals("Token not found or is already used!", exception.getApiError().getError());
        assertNotNull(exception.getApiError().getTimestamp());
        assertNotNull(exception.getApiError().getPath());
    }
}
