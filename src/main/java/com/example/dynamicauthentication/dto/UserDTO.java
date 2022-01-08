package com.example.dynamicauthentication.dto;

import com.example.dynamicauthentication.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private int id;
    private String username;
    private String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().getName();
    }
}
