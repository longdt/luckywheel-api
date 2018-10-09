package com.foxpify.luckywheel.util;

public class ObjectHolder<T> {
    private volatile T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
