package utilities;

import java.util.*;
/**
 * Defines a custom key value couple
 * @param <T> key type
 * @param <U> value type
 */
public class Couple<T, U> implements Map.Entry<T, U> {

    private T key;
    private U value;
    public Couple(T k, U v) {
        key = k;
        value = v;
    }
    @Override
    public T getKey() {
        return key;
    }

    @Override
    public U getValue() {
        return value;
    }

    @Override
    public U setValue(U value) {
        U old = this.value;
        this.value = value;
        return old;
    }
    
}