package com.example.dynamicauthentication.controller;

import com.example.dynamicauthentication.dto.EndpointDTO;
import com.example.dynamicauthentication.service.EndpointService;
import com.example.dynamicauthentication.util.RestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/endpoints")
@RequiredArgsConstructor
public class EndpointController {
    private final EndpointService endpointService;

    @RequestMapping(value = "/roles", method = RequestMethod.PUT)
    public ResponseEntity<?> addRoleToEndPoints(@RequestBody EndpointDTO.AddRollToEndpointsDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestHelper.getValidationErrorsResponse(bindingResult, "Register failed");
        }
        return endpointService.addEndPointsToRole(dto.getRole(), dto.getEndpointIds());
    }
}
