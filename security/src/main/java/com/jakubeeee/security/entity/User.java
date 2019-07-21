package com.jakubeeee.security.entity;

import com.jakubeeee.common.entity.IdentifiableEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a single user of the application.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User extends IdentifiableEntity implements UserDetails {

    @Column(name = "USERNAME", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = true;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false))
    private Set<Role> roles = new HashSet<>();

    // exclude from equals and hashCode methods to prevent cyclic references with password reset tokens
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PasswordResetToken> passwordResetTokens = new HashSet<>();

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new HashSet<GrantedAuthority>();
        for (Role role : roles)
            authorities.add((GrantedAuthority) () -> role.getType().name());
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        // disable java serialization for this class since it is not used
        throw new NotSerializableException(this.getClass().getName());
    }

    private void readObject(ObjectInputStream stream) throws IOException {
        // disable java serialization for this class since it is not used
        throw new NotSerializableException(this.getClass().getName());
    }

}
