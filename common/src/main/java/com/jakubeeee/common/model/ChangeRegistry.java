package com.jakubeeee.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model class whose instances can store information about the change of an object's state.
 */
@Data
@AllArgsConstructor
public class ChangeRegistry<T> {

    private final T oldObject;
    private final T newObject;

}
