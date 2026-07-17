package com.jobportal.security;

import com.jobportal.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wraps our User entity so Spring Security can work with it.
 * The role is exposed as a single GrantedAuthority (ROLE_ADMIN, ROLE_EMPLOYER, etc.)
 * which is what @PreAuthorize("hasRole('ADMIN')") checks against.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String fullName;
    private final Boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.isActive = user.getIsActive();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return isActive; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return isActive; }
}
