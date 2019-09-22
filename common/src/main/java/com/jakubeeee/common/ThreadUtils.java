package com.jakubeeee.common;

import lombok.experimental.UtilityClass;

/**
 * Utility class providing useful static methods related to threads.
 */
@UtilityClass
public final class ThreadUtils {

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Internal error has occurred. Thread has been interrupted");
        }
    }

}
