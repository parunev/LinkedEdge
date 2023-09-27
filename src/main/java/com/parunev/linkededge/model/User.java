package com.parunev.linkededge.model;

import com.parunev.linkededge.model.commons.BaseEntity;
import com.parunev.linkededge.model.enums.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EDGE_USERS")
@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "USER_ID"))
public class User extends BaseEntity implements UserDetails {

    @Column(name = "USERNAME", length = 100, nullable = false)
    private String username;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", length = 100, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 100, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTHORITY", nullable = false)
    private Authority authority;

    @Column(name = "ENABLED", columnDefinition = "boolean default false")
    private boolean isEnabled;

    @Column(name = "MFA_ENABLED", columnDefinition = "boolean default false")
    private boolean mfaEnabled;

    @Column(name = "MFA_SECRET", nullable = false)
    private String mfaSecret;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(authority.getAuthorityName()));
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
