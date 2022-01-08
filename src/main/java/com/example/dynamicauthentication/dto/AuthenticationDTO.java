package com.example.dynamicauthentication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthenticationDTO {
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    @Data
    @NoArgsConstructor
    public static class AuthenticationSuccessDTO {
        private UserDTO user;
        private String accessToken;
        private String refreshToken;
    }
}
