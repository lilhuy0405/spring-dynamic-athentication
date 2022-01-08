package com.example.dynamicauthentication.controller;

import com.example.dynamicauthentication.dto.RoleDTO;
import com.example.dynamicauthentication.service.RoleService;
import com.example.dynamicauthentication.util.RestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createRole(@RequestBody RoleDTO.CreateRoleDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestHelper.getValidationErrorsResponse(bindingResult, "Created role failed");
        }
        return roleService.createRole(dto);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getListRole() {
        return roleService.fetchRoles();
    }

    @RequestMapping(value = "/assign", method = RequestMethod.PUT)
    public ResponseEntity<?> assignToRole(
            @RequestBody RoleDTO.AssignUserDTO dto,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return RestHelper.getValidationErrorsResponse(bindingResult, "Assign user to role");
        }
        return roleService.assignUserToRole(dto);
    }
}
