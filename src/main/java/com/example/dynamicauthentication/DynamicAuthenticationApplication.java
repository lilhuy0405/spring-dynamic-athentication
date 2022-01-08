package com.example.dynamicauthentication;

import com.example.dynamicauthentication.entity.Endpoint;
import com.example.dynamicauthentication.service.AuthenticationService;
import com.example.dynamicauthentication.service.EndpointService;
import com.example.dynamicauthentication.util.RequestHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DynamicAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicAuthenticationApplication.class, args);
    }

    @Bean
    CommandLineRunner run(EndpointService endpointService, AuthenticationService authenticationService) {
        return args -> {
            List<Endpoint> currentEndPoints = RequestHelper.scanEndpoints();
            List<Endpoint> inDatabaseEndPoints = endpointService.getAll();
            HashMap<String, ArrayList<Endpoint>> changes = RequestHelper.detectChanges(inDatabaseEndPoints, currentEndPoints);
            for (Map.Entry<String, ArrayList<Endpoint>> entry : changes.entrySet()) {
                String key = entry.getKey();
                if (key.equals("added")) {
                    endpointService.saveListEndpoints(entry.getValue());
                } else if (key.equals("deleted")) {
                    endpointService.deleteListEndpoint(entry.getValue());
                } else if (key.equals("updated")) {
                    ArrayList<Endpoint> value = entry.getValue();
                    for (Endpoint ep : value) {
                        endpointService.updateEndpoint(ep);
                    }
                }
            }
            authenticationService.registerSuperAdmin("huydzthek", "huyhuy");
            //create public role then assign to /auth/login, /auth/register
            authenticationService.initAuthenticationRoutes();
        };
    }

    @Bean(name = "bCryptPasswordEncoder")
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
