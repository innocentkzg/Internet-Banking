package com.auth.Authentication.config;

import com.auth.Authentication.Entity.User;
import com.auth.Authentication.dao.UserRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender; // Inject the JavaMailSender

    private static final int OTP_VALID_DURATION = 5; // OTP validity time in minutes

    public String generateOtp(User user) {

        String otp = String.format("%06d", new Random().nextInt(999999));
        System.out.println(otp);
        user.setOneTimePassword(otp);
        user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(OTP_VALID_DURATION));
        userRepository.save(user);
        return otp;
    }

    public void sendOtpEmail(String to, String otp) {
        try {
            // Construct the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("senderauthapp@bnr.uat");
            mailMessage.setTo(to);
            mailMessage.setSubject("Your OTP");
            mailMessage.setText("Your OTP is: " + otp + ". It will expire in 5 minutes.");

            // Send the email
            mailSender.send(mailMessage);
        }
        catch (MailException e){
            e.printStackTrace();
        }

    }
}
