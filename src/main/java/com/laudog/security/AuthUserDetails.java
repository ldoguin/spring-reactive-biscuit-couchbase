package com.laudog.security;

import com.laudog.domain.user.UserDto;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AuthUserDetails implements UserDetails {
    private final UserDto userDto;

    @Builder
    public AuthUserDetails(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getEmail() {
        return userDto.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDto.getRoles().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getEmail();
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
        return true;
    }
}
