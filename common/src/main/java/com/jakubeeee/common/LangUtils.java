package com.jakubeeee.common;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Utility class providing various general purpose static methods.
 */
@UtilityClass
public final class LangUtils {

    public static <T> T nvl(@Nullable T nullableObject, @NonNull Supplier<T> reserveObjectSupplier) {
        return nullableObject != null ? nullableObject : requireNonNull(reserveObjectSupplier.get());
    }

    public static <T> T nvl(@Nullable T nullableObject, @NonNull T reserveObject) {
        return nullableObject != null ? nullableObject : requireNonNull(reserveObject);
    }

}
