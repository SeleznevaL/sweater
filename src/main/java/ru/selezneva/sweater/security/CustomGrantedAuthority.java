package ru.selezneva.sweater.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority {
    private static final String PREFIX = "ROLE_";
    private final UserRole userRole;

    @Override
    public String getAuthority() {
        return PREFIX + userRole.name();
    }
}

