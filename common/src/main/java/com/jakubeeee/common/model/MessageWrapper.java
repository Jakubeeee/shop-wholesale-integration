package com.jakubeeee.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class whose objects are used to wrap other objects before sending them using
 * rest or a websocket. It allows to attach additional information to the payload.
 */
@Data
@AllArgsConstructor
public class MessageWrapper {

    private Object payload;
    private Action action;

    public enum Action {
        ADD_TO_STATE, SWAP_STATE
    }

}
