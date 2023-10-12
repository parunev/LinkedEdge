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

/**
 * The `EmailSender` class is responsible for sending emails asynchronously in the application. It utilizes the
 * `JavaMailSender` provided by Spring Framework to handle email sending operations. This class offers a method
 * for sending emails to recipients.
 * @author Martin Parunev
 * @date October 12, 2023
 */
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender sender;
    private final LELogger leLogger = new LELogger(EmailSender.class);

    /**
     * Asynchronously sends an email to a specified recipient.
     *
     * @param to The recipient's email address.
     * @param email The content of the email, including HTML content.
     * @param subject The subject of the email.
     * <p>
     * This method is marked as asynchronous using Spring's `@Async` annotation, allowing email sending operations
     * to be performed in the background. It creates a `MimeMessage` and uses a `MimeMessageHelper` to set email
     * details such as the recipient, subject, and content.
     * @throws EmailSenderException If the email sending process encounters an exception a corresponding error message and status are shown
     */
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
