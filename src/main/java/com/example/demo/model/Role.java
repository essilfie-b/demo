package com.example.demo.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.model.Permission.*;


@RequiredArgsConstructor
public enum Role {

    USER(
            Set.of(
                    USER_CREATE,
                    USER_READ
            )),

    ADMIN(
            Set.of(ADMIN_READ,
                    ADMIN_DELETE,
                    ADMIN_UPDATE,
                    ADMIN_CREATE,
                    MANAGER_CREATE,
                    MANAGER_DELETE,
                    MANAGER_UPDATE,
                    MANAGER_READ)
    ),
    MANAGER(Set.of(
            MANAGER_CREATE,
            MANAGER_DELETE,
            MANAGER_UPDATE,
            MANAGER_READ
    ))
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = permissions
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}

