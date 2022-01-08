package com.example.dynamicauthentication.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER, mappedBy = "roles")
    private Set<Endpoint> endpoints;

    public void addAddEnpoints(List<Endpoint> endpointList) {
        if(this.endpoints == null) {
            this.endpoints = new HashSet<>();
        }
        for (Endpoint endpoint : endpointList) {
            if(this.endpoints.contains(endpoint)) {
                continue;
            }
            this.endpoints.add(endpoint);
        }
    }
}
