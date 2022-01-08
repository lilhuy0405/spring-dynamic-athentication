package com.example.dynamicauthentication.dto;

import com.example.dynamicauthentication.entity.Endpoint;
import com.example.dynamicauthentication.entity.Role;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class EndpointDTO {
    private int id;
    private String path;
    private String method;
    private String name;
    private List<String> roles;

    public EndpointDTO(Endpoint endpoint) {
        this.id = endpoint.getId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.name = endpoint.getName();
        this.roles = endpoint.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    }

    @Data
    public static class AddRollToEndpointsDTO {
        @NotNull(message = "list endpoints id is required")
        private List<Integer> endpointIds;
        @NotBlank(message = "roleId is required")
        private String role;
    }
}
