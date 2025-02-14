package com.carmate.security.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final String email;

    public JwtAuthentication(String email) {
        super(null);
        this.email = email;
        setAuthenticated(true); // Mark as authenticated
    }

    @Override
    public Object getCredentials() {
        return null; // No password needed
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}
