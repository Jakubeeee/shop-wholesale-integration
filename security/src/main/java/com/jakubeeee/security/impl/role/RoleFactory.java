package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.BaseEntityFactory;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity factory used for production of {@link Role} from {@link RoleValue} and vice versa.
 */
@NoArgsConstructor(staticName = "getInstance")
public class RoleFactory extends BaseEntityFactory<Role, RoleValue> {

    @Override
    public Role createEntity(@NonNull RoleValue value) {
        var role = new Role();
        role.setId(value.getDatabaseId());
        role.setType(value.getType());
        return role;
    }

    @Override
    public RoleValue createValue(@NonNull Role entity) {
        return new RoleValue(entity.getId(), entity.getType());
    }

}
