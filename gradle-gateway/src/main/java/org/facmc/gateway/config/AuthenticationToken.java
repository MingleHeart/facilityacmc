package org.facmc.gateway.config;

import org.facmc.gateway.pojo.LoginData;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;
    private LoginData loginData;

    public AuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public AuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials, LoginData loginData) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.loginData = loginData;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public LoginData getLoginData() {
        return this.loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

}
