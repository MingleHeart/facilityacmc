package org.facmc.common.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MyUserDetials implements UserDetails {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private List<AuthRole> roles;
    private Long userId;
    private int isEnable;

    public MyUserDetials(Long userId, String username, List<AuthRole> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public MyUserDetials(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public MyUserDetials(String username, String password, List<AuthRole> roles, int isEnable, long userId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.isEnable = isEnable;
        this.userId = userId;
        this.authorities = authorities;

    }

    public MyUserDetials(String username, String password, Collection<? extends GrantedAuthority> authorities, int isEnable) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isEnable = isEnable;
    }

    public List<AuthRole> getRoles() {
        return roles;
    }

    public void setRoles(List<AuthRole> roles) {
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnable == 1;
    }
}
