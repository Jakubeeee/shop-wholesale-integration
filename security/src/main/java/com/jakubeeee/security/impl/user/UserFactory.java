package com.jakubeeee.security.impl.user;

import com.jakubeeee.common.persistence.BaseEntityFactory;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity factory used for production of {@link User} from {@link UserValue} and vice versa.
 */
@NoArgsConstructor(staticName = "getInstance")
public class UserFactory extends BaseEntityFactory<User, UserValue> {

    @Override
    public User createEntity(@NonNull UserValue value) {
        var user = new User();
        user.setId(value.getDatabaseId());
        user.setUsername(value.getUsername());
        user.setPassword(value.getPassword());
        user.setEmail(value.getEmail());
        user.setEnabled(value.isEnabled());
        user.setRoles(value.getRoles());
        return user;
    }

    @Override
    public UserValue createValue(@NonNull User entity) {
        return new UserValue(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getEmail(), entity.isEnabled(), entity.getRoles());
    }

}
