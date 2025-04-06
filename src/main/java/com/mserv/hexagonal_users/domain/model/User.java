package com.mserv.hexagonal_users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class User {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private List<Phone> phones = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;
    private String token;
    private boolean isActive;

    public User(UUID id, String name, String email, String password, List<Phone> phones, LocalDateTime created, LocalDateTime modified, LocalDateTime lastLogin, String token, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phones = phones != null ? phones : new ArrayList<>();
        this.created = created;
        this.modified = modified;
    }
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.modified = now;
        this.lastLogin = now;
    }

    public void preUpdate() {
        this.modified = LocalDateTime.now();
    }
}