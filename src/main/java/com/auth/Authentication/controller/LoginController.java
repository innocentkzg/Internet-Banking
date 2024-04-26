package com.auth.Authentication.controller;

import com.auth.Authentication.dao.UserRepository;
import com.auth.Authentication.Entity.Role;
import com.auth.Authentication.Entity.User;
import com.auth.Authentication.config.JWTGenerator;
import com.auth.Authentication.dao.RoleRepository;
import com.auth.Authentication.dto.AuthResponseDTO;
import com.auth.Authentication.dto.LoginDto;
import com.auth.Authentication.dto.RegisterDto;
import com.auth.Authentication.dto.UnlockAccountDto;
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

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator=jwtGenerator;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
       try {
           Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
           SecurityContextHolder.getContext().setAuthentication(authentication);
           String token="Bearer " + jwtGenerator.generateToken(authentication);
           return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
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
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

        Role roles= roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
        userRepository.save(user);
        return new ResponseEntity<>("User Registered Successfull!", HttpStatus.OK);
   }
}
