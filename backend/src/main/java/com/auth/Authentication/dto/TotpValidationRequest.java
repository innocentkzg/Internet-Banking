package com.auth.Authentication.dto;

import lombok.Data;

@Data
public class TotpValidationRequest {
    public int totp;
    public String username;
}
