package com.example.demo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        System.out.println("=== EMAIL CONFIG DEBUG ===");
        System.out.println("Username from env: " + System.getenv("SUPPORT_EMAIL"));
        System.out.println("Password from env: " + System.getenv("APP_PASSWORD"));
        System.out.println("========================");

    }



    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);


    }
}
