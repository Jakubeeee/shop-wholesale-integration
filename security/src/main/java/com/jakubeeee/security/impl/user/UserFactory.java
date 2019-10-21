package com.jakubeeee.security.impl.user;

import com.jakubeeee.common.persistence.BaseEntityFactory;
import com.jakubeeee.security.impl.role.RoleFactory;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity factory used for production of {@link User} from {@link UserValue} and vice versa.
 */
@NoArgsConstructor(staticName = "getInstance")
public class UserFactory extends BaseEntityFactory<User, UserValue> {

    private static final RoleFactory roleFactory = RoleFactory.getInstance();

    @Override
    public User createEntity(@NonNull UserValue value) {
        var user = new User();
        user.setId(value.getDatabaseId());
        user.setUsername(value.getUsername());
        user.setPassword(value.getPassword());
        user.setEmail(value.getEmail());
        user.setEnabled(value.isEnabled());
        user.setRoles(roleFactory.createEntities(value.getRoles()));
        return user;
    }

    @Override
    public UserValue createValue(@NonNull User entity) {
        return new UserValue(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getEmail(),
                entity.isEnabled(), roleFactory.createValues(entity.getRoles()));
    }

}
