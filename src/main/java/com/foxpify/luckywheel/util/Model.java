package com.foxpify.luckywheel.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Model {
    public static <T> void copyNonNull(Supplier<T> srcGetter, Consumer<T> destSetter) {
        var val = srcGetter.get();
        if (val != null) {
            destSetter.accept(val);
        }
    }
}
