package com.jakubeeee.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MessageWrapper {

    Object payload;
    Action action;

    public enum Action {
        ADD_TO_STATE, SWAP_STATE
    }

}
