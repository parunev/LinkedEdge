package com.parunev.linkededge.security.mfa;

import com.parunev.linkededge.util.LELogger;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Component;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;
/**
 * @Description: The `Google2FA` class provides two-factor authentication (2FA) functionality using the
 * TOTP (Time-based One-Time Password) algorithm. It allows users to generate a secret, create a QR code for
 * use with authenticator apps, and verify OTP codes.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
public class Google2FA {
    private final LELogger leLogger = new LELogger(Google2FA.class);

    /**
     * Generate a new secret for 2FA.
     *
     * @return A randomly generated secret.
     */
    public String generateNewSecret() {
        leLogger.info("Generating new secret for 2FA");
        return new DefaultSecretGenerator().generate();
    }

    /**
     * Generate a data URI for a QR code image that can be used by authenticator apps.
     *
     * @param secret The secret used for generating OTP codes.
     * @return A data URI containing the QR code image.
     */
    public String generateQrCodeImageUri(String secret) {
        QrData data = new QrData.Builder()
                .label("LinkedEdge GoogleQRCode 2FA")
                .secret(secret)
                .issuer("LinkedEdge-API")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];
        try {
            imageData = generator.generate(data);
            leLogger.info("QR-CODE generated successfully");
        } catch (QrGenerationException e) {
            leLogger.error("Error while generating QR-CODE", e, e.getMessage());
        }

        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    /**
     * Validate an OTP (One-Time Password) code using the provided secret.
     *
     * @param secret The secret used for generating OTP codes.
     * @param code   The OTP code to validate.
     * @return `true` if the provided OTP code is valid, `false` otherwise.
     */
    public boolean isOtpValid(String secret, String code) {
        leLogger.info("Validating OTP code for secret");
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }
}
