package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.IdentifiableEntityValue;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;

/**
 * Immutable value object for {@link Role} data transfer.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Value
public class RoleValue extends IdentifiableEntityValue<Role> {

    private final RoleType type;

    private RoleValue(RoleType type) {
        this(null, type);
    }

    public RoleValue(@Nullable Long databaseId,
                     @NonNull RoleType type) {
        super(databaseId);
        this.type = type;
    }

    public static RoleValue of(RoleType type) {
        return new RoleValue(type);
    }

}
