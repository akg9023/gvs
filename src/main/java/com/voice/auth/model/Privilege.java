package com.voice.auth.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "privileges", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "privilege_resource",
            joinColumns = @JoinColumn(name = "privilege_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> resources;

    // Constructors, getters, and setters
    public Privilege() {}

    public Privilege(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }
}