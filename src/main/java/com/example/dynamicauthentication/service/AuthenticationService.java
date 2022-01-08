package com.example.dynamicauthentication.service;

import com.example.dynamicauthentication.dto.AuthenticationDTO;
import com.example.dynamicauthentication.dto.UserDTO;
import com.example.dynamicauthentication.entity.Endpoint;
import com.example.dynamicauthentication.entity.Role;
import com.example.dynamicauthentication.entity.User;
import com.example.dynamicauthentication.repository.EndpointRepository;
import com.example.dynamicauthentication.repository.RoleRepository;
import com.example.dynamicauthentication.repository.UserRepository;
import com.example.dynamicauthentication.util.JwtUtil;
import com.example.dynamicauthentication.util.PasswordHelper;
import com.example.dynamicauthentication.util.RESTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EndpointRepository endpointRepository;
    private PasswordEncoder bCryptPasswordEncoder = PasswordHelper.getBCryptPasswordEncoder();


    public ResponseEntity<?> registerUser(AuthenticationDTO authenticationDTO) {
        HashMap<String, Object> restResponse;
        Optional<User> inDb = userRepository.findByUsername(authenticationDTO.getUsername());
        if (inDb.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Username has been taken").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        try {
            User user = new User();
            user.setUsername(authenticationDTO.getUsername());
            //hash password
            String passwordHash = bCryptPasswordEncoder.encode(authenticationDTO.getPassword());
            user.setPasswordHash(passwordHash);
            //create a default role then assign user to that role
            String authenticatedRoleName = "authenticated";
            Role authenticatedRole;
            Optional<Role> inDbRoleOptional = roleRepository.findByName(authenticatedRoleName);
            if (!inDbRoleOptional.isPresent()) {
                Role role = new Role();
                role.setName(authenticatedRoleName);
                role.setDescription("Default role assigned for new user");
                authenticatedRole = roleRepository.save(role);
            } else {
                authenticatedRole = inDbRoleOptional.get();
            }
            user.setRole(authenticatedRole);
            User savedUser = userRepository.save(user);
            //login user by giving them jwt token
            //generate tokens
            String accessToken = JwtUtil.generateToken(user.getUsername(),
                    user.getRole().getName(),
                    JwtUtil.ONE_DAY * 7);

            String refreshToken = JwtUtil.generateToken(user.getUsername(),
                    null,
                    JwtUtil.ONE_DAY * 14);
            AuthenticationDTO.AuthenticationSuccessDTO successDTO = new AuthenticationDTO.AuthenticationSuccessDTO();
            successDTO.setAccessToken(accessToken);
            successDTO.setRefreshToken(refreshToken);
            successDTO.setUser(new UserDTO(savedUser));
            restResponse = new RESTResponse.Success()
                    .setMessage("Register new user success")
                    .setStatus(HttpStatus.CREATED.value())
                    .setData(successDTO).build();
            return ResponseEntity.created(null).body(restResponse);
        } catch (Exception exception) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(exception.getMessage()).build();
            return ResponseEntity.internalServerError().body(restResponse);
        }
    }

    public ResponseEntity<?> loginUser(AuthenticationDTO authenticationDTO) {
        HashMap<String, Object> restResponse;
        Optional<User> byUsername = userRepository.findByUsername(authenticationDTO.getUsername());
        if (!byUsername.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Username not found").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        try {
            User user = byUsername.get();
            String inDbPwd = user.getPasswordHash();
            boolean matches = bCryptPasswordEncoder.matches(authenticationDTO.getPassword(), inDbPwd);
            if (matches) {
                //generate tokens
                String accessToken = JwtUtil.generateToken(user.getUsername(),
                        user.getRole().getName(),
                        JwtUtil.ONE_DAY * 7);

                String refreshToken = JwtUtil.generateToken(user.getUsername(),
                        null,
                        JwtUtil.ONE_DAY * 14);
                AuthenticationDTO.AuthenticationSuccessDTO successDTO = new AuthenticationDTO.AuthenticationSuccessDTO();
                successDTO.setAccessToken(accessToken);
                successDTO.setRefreshToken(refreshToken);
                successDTO.setUser(new UserDTO(user));
                restResponse = new RESTResponse.Success()
                        .setMessage("Login success")
                        .setStatus(HttpStatus.OK.value())
                        .setData(successDTO).build();
                return ResponseEntity.ok().body(restResponse);
            } else {
                restResponse = new RESTResponse.CustomError()
                        .setCode(HttpStatus.BAD_REQUEST.value())
                        .setMessage("Wrong password").build();
                return ResponseEntity.badRequest().body(restResponse);
            }
        } catch (Exception exception) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(exception.getMessage()).build();
            return ResponseEntity.internalServerError().body(restResponse);
        }
    }

    public void registerSuperAdmin(String username, String password) {
        Optional<User> inDb = userRepository.findByUsername(username);
        if (inDb.isPresent()) {
            return;
        }
        try {
            User superAdmin = new User();
            superAdmin.setUsername(username);
            //hash password
            String passwordHash = bCryptPasswordEncoder.encode(password);
            superAdmin.setPasswordHash(passwordHash);
            //create a default role then assign user to that role
            String supperAdminRoleName = "supper admin";
            Role supperAdminRole;
            Optional<Role> inDbRoleOptional = roleRepository.findByName(supperAdminRoleName);
            if (!inDbRoleOptional.isPresent()) {
                Role role = new Role();
                role.setName(supperAdminRoleName);
                role.setDescription("Super admin");
                supperAdminRole = roleRepository.save(role);
            } else {
                supperAdminRole = inDbRoleOptional.get();
            }
            superAdmin.setRole(supperAdminRole);
            userRepository.save(superAdmin);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void initAuthenticationRoutes() {
        //create public role then assign to /auth/login, /auth/register
        try {
            String publicRoleName = "public";
            Role publicRole;
            Optional<Role> inDbRoleOptional = roleRepository.findByName(publicRoleName);
            if (!inDbRoleOptional.isPresent()) {
                Role role = new Role();
                role.setName(publicRoleName);
                role.setDescription("public urls");
                publicRole = roleRepository.save(role);
            } else {
                publicRole = inDbRoleOptional.get();
            }
            Optional<Endpoint> loginEndpointOptional
                    = endpointRepository.findByPathAndMethod("/auth/login", "POST");
            if(!loginEndpointOptional.isPresent()) {
                return;
            }
            Endpoint loginEndpoint = loginEndpointOptional.get();

            Optional<Endpoint> registerEndpointOptional
                    = endpointRepository.findByPathAndMethod("/auth/register", "POST");
            if(!registerEndpointOptional.isPresent()) {
                return;
            }
            Endpoint registerEndpoint = registerEndpointOptional.get();
            if(!loginEndpoint.containsRoleName(publicRoleName)) {
                loginEndpoint.addRoles(publicRole);
                endpointRepository.save(loginEndpoint);
            }
            if(!registerEndpoint.containsRoleName(publicRoleName)) {
                registerEndpoint.addRoles(publicRole);
                endpointRepository.save(registerEndpoint);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
