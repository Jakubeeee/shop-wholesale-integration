package com.jakubeeee.common.persistence;

/**
 * Common interface for immutable data transfer objects that have jpa entity equivalent.
 *
 * @see JpaEntity
 */
public interface JpaEntityValue<E extends JpaEntity> extends ImmutableValue {

}
