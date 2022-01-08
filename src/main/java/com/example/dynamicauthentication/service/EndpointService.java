package com.example.dynamicauthentication.service;

import com.example.dynamicauthentication.dto.EndpointDTO;
import com.example.dynamicauthentication.entity.Endpoint;
import com.example.dynamicauthentication.entity.Role;
import com.example.dynamicauthentication.repository.EndpointRepository;
import com.example.dynamicauthentication.repository.RoleRepository;
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
public class EndpointService {
    private final EndpointRepository endpointRepository;
    private final RoleRepository roleRepository;

    public void saveListEndpoints(List<Endpoint> endpointList) {
        endpointRepository.saveAll(endpointList);
    }

    public List<Endpoint> getAll() {
        return endpointRepository.findAll();
    }

    public void deleteListEndpoint(List<Endpoint> list) {
        endpointRepository.deleteAll(list);
    }

    public void updateEndpoint(Endpoint endpoint) {
        int id = endpoint.getId();
        Optional<Endpoint> byId = endpointRepository.findById(id);
        Endpoint toUpdate = byId.orElse(null);
        toUpdate.setName(endpoint.getName());
        toUpdate.setPath(endpoint.getPath());
        toUpdate.setMethod(endpoint.getMethod());
        endpointRepository.save(toUpdate);
    }

    public ResponseEntity<?> addEndPointsToRole(String roleName, List<Integer> endpointIds) {
        HashMap<String, Object> restResponse;
        List<Endpoint> listEndpoints = endpointRepository.findAllById(endpointIds);
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (!roleOptional.isPresent()) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Role not found").build();
            return ResponseEntity.badRequest().body(restResponse);
        }
        try {
            Role role = roleOptional.get();
            role.addAddEnpoints(listEndpoints);
            for (Endpoint ep :
                    listEndpoints) {
                ep.addRoles(role);
            }
            List<Endpoint> listUpdated = endpointRepository.saveAll(listEndpoints);
            List<EndpointDTO> listDTOs = listUpdated.stream().map(EndpointDTO::new).collect(Collectors.toList());
            restResponse = new RESTResponse.Success()
                    .setMessage("Updated success")
                    .setStatus(HttpStatus.OK.value())
                    .setData(listDTOs).build();
            return ResponseEntity.ok().body(restResponse);
        } catch (Exception exception) {
            restResponse = new RESTResponse.CustomError()
                    .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .setMessage(exception.getMessage()).build();
            return ResponseEntity.internalServerError().body(restResponse);
        }
    }
}
