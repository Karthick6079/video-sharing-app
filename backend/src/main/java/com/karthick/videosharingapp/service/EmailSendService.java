package com.karthick.videosharingapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class EmailSendService {


    private final Logger logger = LoggerFactory.getLogger(EmailSendService.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendWelcomeEmail(String toEmail, String userName) {
        Context context = new Context();
        context.setVariable("name", userName);

        String htmlContent = templateEngine.process("welcome-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(toEmail);
            helper.setSubject(" Welcome to AVK Video Sharing App!🎉");
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);

            logger.info("Sending welcome email to new user!");
            mailSender.send(message);
            logger.info("Email sent successfully!!");
        } catch (MessagingException e) {
            logger.error("Exception occurred during sending user welcome email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
