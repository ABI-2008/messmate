package com.sece.expert.dto;

import jakarta.validation.constraints.NotBlank;

public class SignInRequest {

    @NotBlank(message = "Username or Email is required")
    private String identifier; // accepts either username or email

    @NotBlank(message = "Password is required")
    private String password;

    public SignInRequest() {
    }

    public SignInRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
