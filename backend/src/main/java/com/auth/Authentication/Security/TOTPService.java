package com.auth.Authentication.Security;

import com.auth.Authentication.dao.UserRepository;
import com.auth.Authentication.Entity.User;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TOTPService {

    @Autowired
    private UserRepository userRepository;

    public String generateTotpSecret(User user) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String totp = key.getKey();
        System.out.println(totp);
        user.setTotpSecret(totp);
        userRepository.save(user);
        return totp;
    }

    public boolean verifyTOTP(User user, int otp) {
        if (user == null || user.getTotpSecret() == null) {
            return false;
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(user.getTotpSecret(), otp);
    }
}
