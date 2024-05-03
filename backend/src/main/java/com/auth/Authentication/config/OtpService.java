package com.auth.Authentication.config;

import com.auth.Authentication.Entity.User;
import com.auth.Authentication.dao.UserRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private UserRepository userRepository;



    @Autowired
    private JavaMailSender mailSender; // Inject the JavaMailSender

    private static final int OTP_VALID_DURATION = 5; // OTP validity time in minutes
    private static final String SERVICE_URL = "http://172.16.1.142:7003/Sms_Bridge/ProxyService/Sms_Proxy_Service"; // Replace with your actual service URL
    private static final String USERNAME = "BNR_ECP";
    private static final String PASSWORD = "Bnrecp123!";

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
    public void sendOtpSms(String phoneNumber, String otp) {
        try {
            URL url = new URL(SERVICE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set HTTP method to POST and specify that this is a SOAP request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setDoOutput(true);

            // Construct SOAP XML for sending OTP
            String soapXml = constructSoapRequest(phoneNumber, otp);
            byte[] xmlBytes = soapXml.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(xmlBytes.length));

            // Send SOAP request
            OutputStream os = connection.getOutputStream();
            os.write(xmlBytes);
            os.close();

            // Check response from server
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("OTP sent successfully!");
            } else {
                System.out.println("Failed to send OTP. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String constructSoapRequest(String phoneNumber, String otp) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sms=\"SMSBridgeService\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<sms:SendSMS>" +
                "<sms:number>" + phoneNumber + "</sms:number>" +
                "<sms:text>Your OTP is: " + otp + ". It will expire in 5 minutes.</sms:text>" +
                "<sms:sms_title>Bnr OTP</sms:sms_title>" +
                "<sms:sms_id>92</sms:sms_id>" +
                "<sms:lgn>" + USERNAME + "</sms:lgn>" +
                "<sms:pwd>" + PASSWORD + "</sms:pwd>" +
                "</sms:SendSMS>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
    }
}
