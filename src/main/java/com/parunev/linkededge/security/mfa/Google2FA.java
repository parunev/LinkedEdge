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

@Component
public class Google2FA {
    private final LELogger leLogger = new LELogger(Google2FA.class);


    public String generateNewSecret() {
        leLogger.info("Generating new secret for 2FA");
        return new DefaultSecretGenerator().generate();
    }

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

    public boolean isOtpValid(String secret, String code) {
        leLogger.info("Validating OTP code for secret");
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }
}
