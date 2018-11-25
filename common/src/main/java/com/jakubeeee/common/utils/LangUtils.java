package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jakubeeee.common.utils.DateTimeUtils.formatTime;
import static java.time.LocalDateTime.now;
import static java.util.Collections.binarySearch;
import static java.util.Collections.sort;
import static java.util.Objects.requireNonNull;

@UtilityClass
public class LangUtils {

    public static void print(Object object) {
        System.out.println(formatTime(now()) + " " + Thread.currentThread().getName() + " " + object.toString());
    }

    public static <T> T nvl(@Nullable T nullableObject, Supplier<T> reserveObjectSupplier) {
        return nullableObject != null ? nullableObject : requireNonNull(reserveObjectSupplier.get());
    }

    public static <T> T nvl(@Nullable T nullableObject, T reserveObject) {
        return nullableObject != null ? nullableObject : requireNonNull(reserveObject);
    }

    public static <T> List<T> cloneList(List<T> list) {
        return new ArrayList<>(list);
    }

    @SafeVarargs
    public static <T> List<T> toList(T... objects) {
        return Arrays.asList(objects);
    }

    public static <T> List<T> extractList(List<T> list) {
        List<T> extractedList = new ArrayList<>(list);
        list.clear();
        return extractedList;
    }

    public static <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T extends Comparable<T>> Optional<T> findMatchInList(List<T> list, T object) {
        return findMatchInList(list, object, false);
    }

    public static <T extends Comparable<T>> Optional<T> findMatchInList(List<T> list, T object, boolean checkIfSorted) {
        T result = null;
        if (checkIfSorted && !isListSorted(list)) sort(list);
        int index = binarySearch(list, object);
        if (index >= 0) result = list.get(index);
        return Optional.ofNullable(result);
    }

    public static <T extends Comparable<T>> boolean isListSorted(List<T> list) {
        boolean isSorted = true;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).compareTo(list.get(i)) > 0) {
                isSorted = false;
                break;
            }
        }
        return isSorted;
    }

    @SafeVarargs
    public static Map<String, Object> mergeMaps(Map<String, Object>... maps) {
        var mergedMap = new HashMap<String, Object>();
        for (var map : maps) {
            map.forEach((k, v) -> mergedMap.merge(k, v, (v1, v2) -> {
                throw new AssertionError("Merged maps have duplicate keys: " + k);
            }));
        }
        return mergedMap;
    }

}


