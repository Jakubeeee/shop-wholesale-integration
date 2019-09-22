package com.jakubeeee.common;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.binarySearch;
import static java.util.Collections.sort;

/**
 * Utility class providing useful static methods for operations on collections.
 */
@UtilityClass
public final class CollectionUtils {

    public static <T> List<T> shallowCloneList(@NonNull List<T> list) {
        return new ArrayList<>(list);
    }

    public static <T> List<T> extractList(@NonNull List<T> list) {
        List<T> extractedList = new ArrayList<>(list);
        list.clear();
        return extractedList;
    }

    public static <T> List<T> filterList(@NonNull List<T> list, @NonNull Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T extends Comparable<T>> Optional<T> findMatchInList(@NonNull List<T> list, @NonNull T object) {
        return findMatchInList(list, object, false);
    }

    public static <T extends Comparable<T>> Optional<T> findMatchInList(@NonNull List<T> list,
                                                                        @NonNull T object,
                                                                        @NonNull boolean checkIfSorted) {
        T result = null;
        if (checkIfSorted && !isListSorted(list)) sort(list);
        int index = binarySearch(list, object);
        if (index >= 0) result = list.get(index);
        return Optional.ofNullable(result);
    }

    public static <T extends Comparable<T>> boolean isListSorted(@NonNull List<T> list) {
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
    public static Map<String, Object> mergeMaps(@NonNull Map<String, Object>... maps) {
        var mergedMap = new HashMap<String, Object>();
        for (var map : maps) {
            map.forEach((k, v) -> mergedMap.merge(k, v, (v1, v2) -> {
                throw new AssertionError("Merged maps have duplicate keys: " + k);
            }));
        }
        return mergedMap;
    }

    public static SortedMap<String, Object> sortedMapOf(@NonNull Map<String, Object> map) {
        return new TreeMap<>(map);
    }

}


