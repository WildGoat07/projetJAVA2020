package utilities;

import java.util.function.Function;

/**
 * Store an object, but gives a different toString() value
 * @param <T> type of the stored object
 */
public class ToStringOverrider<T> {
    private T obj;
    private Function<T, String> toStr;
    /**
     * Constructor
     * @param object stored object
     * @param tostring toString overrider
     */
    public ToStringOverrider(T object, Function<T, String> tostring) {
        obj = object;
        toStr = tostring;
    }
    /**
     * Constructor
     * @param object stored object
     * @param toString value of toString
     */
    public ToStringOverrider(T object, String toString) {
        this(object, (o) -> toString);
    }
    @Override
    public String toString() {
        return toStr.apply(obj);
    }
    /**
     * Gets the stored object
     * @return the stored object
     */
    public T getObject() {
        return obj;
    }
}