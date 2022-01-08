package com.example.dynamicauthentication.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String path;
    private String method;
    private String name;

    public Endpoint(String path, String method, String name) {
        this.path = path;
        this.method = method;
        this.name = name;
    }

    public String getKey() {
        return this.method.concat(this.path);
    }

    public boolean equals(Endpoint toCompare) {
        return this.path.equals(toCompare.getPath()) && this.method.equals(toCompare.getMethod())
                && this.name.equals(toCompare.getName());
    }
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "endpoint_role",
            joinColumns = @JoinColumn(name = "endpoint_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Override
    public String toString() {
        return "Endpoint{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public void addRoles(Role role) {
        if(this.roles == null) {
            this.roles = new HashSet<Role>();
        }
        this.roles.add(role);
    }

    public boolean containsRoleName(String roleName) {
        boolean res = false;
        if(this.roles == null || this.roles.isEmpty()) {
            return false;
        }
        for (Role role : this.roles) {
            String name = role.getName();
            if(name.equals(roleName)) {
                res = true;
            }
        }
        return res;
    }
}
