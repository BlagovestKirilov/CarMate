package com.carmate.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
        LOGGER.info("Email sent to: {}", email);
    }

    public void sendEmailWithPdfAttachment(String email, String subject, String body, byte[] pdfBytes, String fileName) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

            javaMailSender.send(message);
            LOGGER.info("Email with pdf attachment sent to: {}", email);
        } catch (MessagingException e) {
            LOGGER.info("Email with pdf attachment sending failed to: {}", email, e);
        }
    }
}
