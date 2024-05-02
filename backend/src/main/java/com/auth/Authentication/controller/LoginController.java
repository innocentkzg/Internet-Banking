package com.auth.Authentication.controller;

import com.auth.Authentication.Security.TOTPService;
import com.auth.Authentication.dao.UserRepository;
import com.auth.Authentication.Entity.Role;
import com.auth.Authentication.Entity.User;
import com.auth.Authentication.config.JWTGenerator;
import com.auth.Authentication.dao.RoleRepository;
import com.auth.Authentication.dto.*;
import com.auth.Authentication.config.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    public static final int MAX_ATTEMPTS = 3;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private JWTGenerator jwtGenerator;

    private OtpService otpService;

    private TOTPService totpService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JWTGenerator jwtGenerator,
                           OtpService otpService,
                           TOTPService totpService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator=jwtGenerator;
        this.otpService=otpService;
        this.totpService=totpService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
       try {
           Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
           SecurityContextHolder.getContext().setAuthentication(authentication);
           String token="Bearer " + jwtGenerator.generateToken(authentication);

           // Upon successful authentication
           User user = userRepository.findByUsername(loginDto.getUsername()).get();
           String otp = otpService.generateOtp(user);
           String totp = totpService.generateTotpSecret(user);
           System.out.println("otp");
           System.out.println("otp");
           otpService.sendOtpEmail(user.getEmail(), otp);

           return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
           //return ResponseEntity.status(HttpStatus.OK).body("OTP has been sent to your email.");

       }
       catch (BadCredentialsException e){
           Optional<User> user = userRepository.findByUsername(loginDto.getUsername());
           if (user.isPresent()) {
               User user1=user.get();
               int attempts = user1.getFailedLoginAttempts();
               if (attempts < MAX_ATTEMPTS - 1) {
                   user1.setFailedLoginAttempts(attempts + 1);
                   userRepository.save(user1);
               } else {
                   user1.setAccountLocked(true);
                   userRepository.save(user1);
                   // Optionally send a notification to the user here.
               }
           }
           throw e; // Re-throw the exception to be handled by the global exception handler
       }

    }

    @PostMapping("/generate")
    public String generateTOTP(@RequestBody TOTPRequestDto totpRequestDto) {
        User user = userRepository.findByUsername(totpRequestDto.getUsername()).get();
        return totpService.generateTotpSecret(user);
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<String> verifyTOTP(@RequestBody TotpValidationRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).get();
        if (totpService.verifyTOTP(user, request.getTotp())) {
            return ResponseEntity.ok("TOTP verification successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }
    }
    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest request) {

        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        String  username = auth.getName();
        User user = userRepository.findByUsername(request.getUsername()).get();
        if (user.isOtpValid() && user.getOneTimePassword().equals(request.getOtp())) {
            // OTP is valid
            String jwtToken = jwtGenerator.createToken(username);
            return new ResponseEntity<>(new AuthResponseDTO(jwtToken), HttpStatus.OK);
        } else {
            // OTP is invalid or expired
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
        }
    }

    @PostMapping("/generate-new-otp")
    public ResponseEntity<?> generateNewOtp(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).get();
        String newOtp = otpService.generateOtp(user);
        otpService.sendOtpEmail(user.getEmail(), newOtp);
        return ResponseEntity.ok("New OTP has been sent to your email.");
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockAccount(@RequestBody UnlockAccountDto unlockAccountDto) {
        Optional<User> user = userRepository.findByUsername(unlockAccountDto.getUsername());
        if (user.isPresent() && user.get().isAccountLocked()) {
            User user1 = user.get();
            user1.setAccountLocked(false);
            user1.setFailedLoginAttempts(0);
            userRepository.save(user1);
            return ResponseEntity.ok("Account unlocked successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account is not locked or does not exist.");
        }
    }
   @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsByusername(registerDto.getUsername())){
            return new ResponseEntity<>("Username Exists", HttpStatus.BAD_REQUEST);
        }

       User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Role roles= roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
        userRepository.save(user);
        return new ResponseEntity<>("User Registered Successfull!", HttpStatus.OK);
   }
}
