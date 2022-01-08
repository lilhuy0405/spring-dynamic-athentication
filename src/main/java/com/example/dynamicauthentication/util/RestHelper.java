package com.example.dynamicauthentication.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;

public class RestHelper {
    public static HashMap<String, String> getValidationErrors(BindingResult bindingResult) {
        HashMap<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    public static ResponseEntity<Object> getValidationErrorsResponse(BindingResult bindingResult, String message) {
        HashMap<String, String> validationErrors = getValidationErrors(bindingResult);
        HashMap<String, Object> resp = new RESTResponse.ValidateError().
                addErrors(validationErrors)
                .setMessage(message)
                .build();
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
