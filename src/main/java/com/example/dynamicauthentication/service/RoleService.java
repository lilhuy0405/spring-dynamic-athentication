package com.example.dynamicauthentication.service;

import com.example.dynamicauthentication.dto.RoleDTO;
import com.example.dynamicauthentication.dto.UserDTO;
import com.example.dynamicauthentication.entity.Role;
import com.example.dynamicauthentication.entity.User;
import com.example.dynamicauthentication.repository.RoleRepository;
import com.example.dynamicauthentication.repository.UserRepository;
import com.example.dynamicauthentication.util.RESTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> createRole(RoleDTO.CreateRoleDTO newRole) {
        HashMap<String, Object> restResponse;
        Optional<Role> byName = roleRepository.findByName(newRole.getName());
        if (byName.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Role name has been taken").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        try {
            Role role = new Role();
            role.setName(newRole.getName());
            role.setDescription(newRole.getDescription());
            Role savedRole = roleRepository.save(role);
            restResponse = new RESTResponse.Success()
                    .setMessage("Created role success")
                    .setStatus(HttpStatus.CREATED.value())
                    .setData(new RoleDTO(savedRole)).build();
            return ResponseEntity.created(null).body(restResponse);
        } catch (Exception ex) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(ex.getMessage()).build();
            return ResponseEntity.internalServerError().body(restResponse);
        }
    }

    public ResponseEntity<?> fetchRoles() {
        List<Role> all = roleRepository.findAll();
        List<RoleDTO> listRoleDTOs = all.stream().map(item -> new RoleDTO(item)).collect(Collectors.toList());
        HashMap<String, ?> restResponse = new RESTResponse.Success()
                .setMessage("OK")
                .setStatus(HttpStatus.OK.value())
                .setData(listRoleDTOs).build();
        return ResponseEntity.ok().body(restResponse);
    }

    public ResponseEntity<?> assignUserToRole(RoleDTO.AssignUserDTO dto) {
        HashMap<String, Object> restResponse;
        Optional<Role> roleOptional = roleRepository.findByName(dto.getRole());
        if (!roleOptional.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Role not found").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if (!userOptional.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("User not found").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        try {
            Role role = roleOptional.get();
            User user = userOptional.get();
            user.setRole(role);
            User updatedUser = userRepository.save(user);
            restResponse = new RESTResponse.Success()
                    .setMessage("Updated success")
                    .setStatus(HttpStatus.OK.value())
                    .setData(new UserDTO(updatedUser)).build();
            return ResponseEntity.ok().body(restResponse);
        } catch (Exception exception) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(exception.getMessage()).build();
            return ResponseEntity.internalServerError().body(restResponse);
        }
    }
}
