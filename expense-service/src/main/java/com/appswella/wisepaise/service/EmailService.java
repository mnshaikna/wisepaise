package com.appswella.wisepaise.service;

import com.appswella.wisepaise.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPinResetEmail(User user, String token) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String htmlContent = getString(token, user.getUserName());

            helper.setTo(user.getUserEmail());
            helper.setSubject("Reset Your Group PIN");
            helper.setFrom("noreply@wisepaisa.com");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private static String getString(String token, String name) {
        String resetLink = "http://wisepaisa.cloud:8083/users/reset-pin?token=" + token;

        return "<p>Hello " + name + ",<br/>Click the button below to reset your Group PIN." + "<br/></p>" + "<br/>" + "<a href=\"" + resetLink + "\" " + "style=\"display:inline-block;" + "padding:10px 20px;" + "background:#4CAF50;" + "color:white;" + "text-decoration:none;" + "border-radius:6px;" + "font-weight:bold;\">" + "Reset PIN</a>";
    }
}

