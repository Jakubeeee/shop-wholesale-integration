package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.IdentifiableEntity;
import com.jakubeeee.security.impl.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing role that can be assigned to users. They allow access to system
 * components only for users with appropriate permissions.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "ROLES")
public class Role extends IdentifiableEntity {

    @Column(name = "TYPE", unique = true, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    // exclude from equals, hashCode and toString methods to prevent cyclic references with users
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    private Role(Type type) {
        this.type = type;
    }

    public static Role of(Type type) {
        return new Role(type);
    }

    public enum Type {
        BASIC_USER,
        PRO_USER,
        ADMIN
    }

}
