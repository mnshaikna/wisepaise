package com.appswella.wisepaise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPinResetEmail(String toEmail, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Your PIN");
        message.setText("Click the link below to reset your PIN:\n\n" + resetLink);

        mailSender.send(message);
    }
}

