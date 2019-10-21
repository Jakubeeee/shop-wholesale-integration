package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.IdentifiableEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Entity representing role that can be assigned to users. They allow access to system
 * components only for users with appropriate permissions.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "ROLES")
public class Role extends IdentifiableEntity {

    @Column(name = "TYPE", unique = true, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RoleType type;

    private Role(RoleType type) {
        this.type = type;
    }

    public static Role of(RoleType type) {
        return new Role(type);
    }

}
