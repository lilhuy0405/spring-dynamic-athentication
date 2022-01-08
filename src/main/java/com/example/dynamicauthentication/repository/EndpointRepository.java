package com.example.dynamicauthentication.repository;

import com.example.dynamicauthentication.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.StringReader;
import java.util.Optional;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Integer> {
    Optional<Endpoint> findByPathAndMethod(String path, String method);
}
