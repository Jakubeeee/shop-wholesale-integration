package com.jakubeeee.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Model class whose instances can store information about the change of an object's state
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChangeRegistry<T> {

    T oldObject;
    T newObject;

}
