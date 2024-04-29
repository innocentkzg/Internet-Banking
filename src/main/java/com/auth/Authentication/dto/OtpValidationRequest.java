package com.auth.Authentication.dto;

import lombok.Data;

@Data
public class OtpValidationRequest {
    public String otp;
    public String username;
}
