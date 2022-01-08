package com.example.dynamicauthentication.dto;

import com.example.dynamicauthentication.entity.Role;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RoleDTO {
    private int id;
    private String name;
    private String description;
    private List<EndpointDTO> endpoints = new ArrayList<>();

    public RoleDTO(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        if (role.getEndpoints() != null && !role.getEndpoints().isEmpty()) {
            this.endpoints = role.getEndpoints().stream().map(item -> new EndpointDTO(item)).collect(Collectors.toList());
        }
    }

    @Data
    public static class CreateRoleDTO {
        @NotBlank(message = "name is required")
        private String name;
        @NotBlank(message = "description is required")
        private String description;
    }
    @Data
    public static class AssignUserDTO {
        @NotNull(message = "userId is required")
        private int userId;
        @NotBlank(message = "userId is required")
        private String role;
    }
}
