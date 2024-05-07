package com.auth.Authentication.Entity;


import com.auth.Authentication.Enums.Second_factor_auth_option;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="password")
    private String password;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "one_time_password")
    private String oneTimePassword;

    @Column(name = "otp_expiration_time")
    private LocalDateTime otpExpirationTime;

    @Column(name = "totp_secret")
    private String totpSecret;

    @Enumerated(EnumType.STRING)
    private Second_factor_auth_option authType;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public boolean isOtpValid() {
        return this.oneTimePassword != null && this.getOtpExpirationTime().isAfter(LocalDateTime.now());
    }

}
