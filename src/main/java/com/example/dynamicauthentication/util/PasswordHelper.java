package com.example.dynamicauthentication.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHelper {
    private static PasswordEncoder passwordEncoder;
    public static PasswordEncoder getBCryptPasswordEncoder() {
        if(passwordEncoder == null) {
            passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder;
        }
        return passwordEncoder;
    }
}
