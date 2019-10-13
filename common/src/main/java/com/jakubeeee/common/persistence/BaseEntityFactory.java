package com.jakubeeee.common.persistence;

import lombok.NonNull;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

/**
 * Base class used for conversion from jpa entities to immutable value objects after retrieving them from database or
 * conversion from immutable value objects to jpa entities in order to save them in database. Using immutable objects
 * in main application flow is more reliable, safer and free of unnecessary database related information.
 */
public abstract class BaseEntityFactory<E extends JpaEntity, V extends JpaEntityValue<E>> {

    public final Set<E> createEntities(@NonNull Set<V> values) {
        return values.stream()
                .map(this::createEntity)
                .collect(toSet());
    }

    public final List<E> createEntities(@NonNull Iterable<V> values) {
        return stream(values.spliterator(), false)
                .map(this::createEntity)
                .collect(toList());
    }

    public abstract E createEntity(@NonNull V value);

    public final Set<V> createValues(@NonNull Set<E> values) {
        return values.stream()
                .map(this::createValue)
                .collect(toSet());
    }

    public final List<V> createValues(@NonNull Iterable<E> values) {
        return stream(values.spliterator(), false)
                .map(this::createValue)
                .collect(toList());
    }

    public abstract V createValue(@NonNull E entity);

}