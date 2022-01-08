package com.example.dynamicauthentication.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dynamicauthentication.entity.Endpoint;
import com.example.dynamicauthentication.entity.Role;
import com.example.dynamicauthentication.entity.User;
import com.example.dynamicauthentication.repository.UserRepository;
import com.example.dynamicauthentication.service.EndpointService;
import com.example.dynamicauthentication.util.JwtUtil;
import com.example.dynamicauthentication.util.RESTResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {
    private final EndpointService endpointService;
    private final UserRepository userRepository;
    private static final String SUPER_ADMIN_ROLE_NAME = "supper admin";
    private static final String PUBLIC_ROLE_NAME = "public";
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       /* PSEUDO CODE
        1. get all api end point in database and it's roles
        2. Loop through each end point pattern then use antMatcher to compare each end point with current request URI
        2. if role of api include public => pass through
        3. else get jwt token and decode to get username and check role of user in database
        4. if role of user is super admin => pass through
        5. else if api end point's role includes current user's role -> pass through
        6. else send un authorized error
        */
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String requestUrl = req.getRequestURI();
        if (requestUrl.endsWith("/")) {
            requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
        }
        String requestMethod = req.getMethod();
        List<Endpoint> endpoints = endpointService.getAll();
        Endpoint processingEndpoint = null;
        for (Endpoint ep : endpoints) {
            String currentRequestPattern = ep.getPath();
            String currentRequestMethod = ep.getMethod();
            if (antPathMatcher.match(currentRequestPattern, requestUrl) && requestMethod.equals(currentRequestMethod)) {
                processingEndpoint = ep;
            }
        }
        if (processingEndpoint == null) {
            resp.sendError(HttpStatus.NOT_FOUND.value(), "404 not found");
            return;
        }
        if (processingEndpoint.containsRoleName(PUBLIC_ROLE_NAME)) {
            System.out.println("is public");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // get jwt token and decode to get username and check role of user in database
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            resp.sendError(HttpStatus.FORBIDDEN.value(), "no jwt token found");
            return;
        }
        try {
            String token = authorizationHeader.replace("Bearer", "").trim();
            DecodedJWT decodedJWT = JwtUtil.getDecodedJwt(token);
            String username = decodedJWT.getSubject();
            Optional<User> byUsername = userRepository.findByUsername(username);
            if (!byUsername.isPresent()) {
                throw new Exception("Wrong token");
            }
            String userRole = decodedJWT.getClaim(JwtUtil.ROLE_CLAIM_KEY).asString();
            if (userRole.equals(SUPER_ADMIN_ROLE_NAME)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            if (processingEndpoint.containsRoleName(userRole)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            resp.sendError(HttpStatus.UNAUTHORIZED.value(), "UnAuthorized");
            return;
        } catch (Exception ex) {
            //show error
            System.err.println(ex.getMessage());
            resp.setStatus(HttpStatus.FORBIDDEN.value());
            HashMap<String, Object> errorBody = new RESTResponse.CustomError()
                    .setCode(HttpStatus.FORBIDDEN.value())
                    .setMessage(ex.getMessage())
                    .build();
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(resp.getOutputStream(), errorBody);
            return;
        }
    }
}
