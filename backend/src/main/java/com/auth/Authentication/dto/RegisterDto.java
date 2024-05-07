package com.auth.Authentication.dto;


import com.auth.Authentication.Enums.Second_factor_auth_option;
import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private Second_factor_auth_option authType;
}
