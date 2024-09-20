package com.Xandred.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String jwt;
    private boolean status;
    private String message;
    private boolean isTwoFactorAuthEnabled;
    private String session;

    // Constructor for status and message (used in error cases, e.g. email already exists)
    public AuthResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    // Constructor for successful login or registration with JWT
    public AuthResponse(boolean status, String message, String jwt) {
        this.status = status;
        this.message = message;
        this.jwt = jwt;
    }

    // Constructor for cases where two-factor authentication is enabled
    public AuthResponse(boolean status, String message, String jwt, boolean isTwoFactorAuthEnabled, String session) {
        this.status = status;
        this.message = message;
        this.jwt = jwt;
        this.isTwoFactorAuthEnabled = isTwoFactorAuthEnabled;
        this.session = session;
    }
}
