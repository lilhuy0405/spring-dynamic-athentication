package com.example.dynamicauthentication.controller;

import com.example.dynamicauthentication.dto.AuthenticationDTO;
import com.example.dynamicauthentication.service.AuthenticationService;
import com.example.dynamicauthentication.util.RestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestHelper.getValidationErrorsResponse(bindingResult, "Register failed");
        }
        return authenticationService.registerUser(authenticationDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO authenticationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestHelper.getValidationErrorsResponse(bindingResult, "Login failed");
        }
        return authenticationService.loginUser(authenticationDTO);
    }
}
