package utilities;

import java.util.*;
import java.util.function.Predicate;

import data.*;

/**
 * Utility functions
 */
public class Functions {
    private Functions(){}
    /**
     * Returns a new list containing only the items that check the predicate
     * @param <T> type of the items in the list
     * @param list list of items do check
     * @param test predicate to test every item
     * @return the new list after the predicate test
     */
    public static <T> List<T> where(Iterable<T> list, Predicate<T> test) {
        final List<T> result = new ArrayList<T>();
        list.forEach((p) -> {
            if (test.test(p))
                result.add(p);
        });
        return result;
    }
}