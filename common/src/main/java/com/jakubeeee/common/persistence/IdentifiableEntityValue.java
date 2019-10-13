package com.jakubeeee.common.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Superclass for immutable data transfer objects that hold reference to their entity equivalent's primary key.
 * If the primary key is <code>null</code> then it means that this object was not created based on its database equivalent.
 *
 * @see IdentifiableEntity
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public abstract class IdentifiableEntityValue<E extends IdentifiableEntity> implements JpaEntityValue<E> {

    @Getter
    private final Long databaseId;

    public boolean hasDatabaseEquivalent() {
        return databaseId != null;
    }

}
