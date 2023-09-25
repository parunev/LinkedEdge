package com.parunev.linkededge.util.email;

import com.parunev.linkededge.security.exceptions.EmailSenderException;
import com.parunev.linkededge.security.payload.ApiError;
import com.parunev.linkededge.util.LELogger;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.parunev.linkededge.util.RequestUtil.getCurrentRequest;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender sender;
    private final LELogger leLogger = new LELogger(EmailSender.class);

    @Async
    public void send(String to, String email, String subject) {
        leLogger.info("Sending email to: " + to);

        try{
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("parunev@gmail.com");

            leLogger.info("Email sent to: " + to);
            sender.send(mimeMessage);

        } catch (MessagingException e) {
            leLogger.error("Failed to send email", e);

            throw new EmailSenderException(ApiError.builder()
                    .path(getCurrentRequest())
                    .error("Failed to send email. %s".formatted(e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

}
