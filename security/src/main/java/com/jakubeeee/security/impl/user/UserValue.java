package com.jakubeeee.security.impl.user;

import com.jakubeeee.common.persistence.IdentifiableEntityValue;
import com.jakubeeee.security.impl.role.Role;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable value object for {@link User} data transfer.
 */
@SuppressWarnings("squid:S2055" /* this class is not serializable anyway */)
@EqualsAndHashCode(callSuper = false)
@ToString
@Value
public final class UserValue extends IdentifiableEntityValue<User> implements UserDetails {

    private final transient String username;

    private final transient String password;

    private final transient String email;

    private final transient boolean enabled;

    private final transient Set<Role> roles;

    public UserValue(@Nullable Long databaseId,
                     @NonNull String username,
                     @NonNull String password,
                     @NonNull String email,
                     boolean enabled,
                     @NonNull Set<Role> roles) {
        super(databaseId);
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new HashSet<GrantedAuthority>();
        for (Role role : roles)
            authorities.add(() -> role.getType().name());
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

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
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
