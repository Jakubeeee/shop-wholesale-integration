package com.jakubeeee.common.factory;

import com.jakubeeee.common.entity.JpaEntity;
import com.jakubeeee.common.model.ImmutableValue;
import lombok.NonNull;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Base class used for conversion from jpa entities to immutable value objects after retrieving them from database or
 * conversion from immutable value objects to jpa entities in order to save them in database. Using immutable objects
 * in main application flow is more reliable, safer and free of unnecessary database related information.
 */
public abstract class AbstractEntityFactory<E extends JpaEntity, V extends ImmutableValue<E>> {

    public List<E> createEntities(@NonNull Iterable<V> values) {
        return stream(values.spliterator(), false)
                .map(this::createEntity)
                .collect(toList());
    }

    public abstract E createEntity(@NonNull V value);

    public List<V> createValues(@NonNull Iterable<E> values) {
        return stream(values.spliterator(), false)
                .map(this::createValue)
                .collect(toList());
    }

    public abstract V createValue(@NonNull E entity);

}