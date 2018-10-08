package com.foxpify.luckywheel.validate;


import com.foxpify.luckywheel.exception.ValidateException;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Validator<T> extends Function<T, T> {
    static void requireNull(Object value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value != null) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonNull(Object value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonEmpty(String value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || "".equals(value)) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonEmpty(Collection<?> value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value.isEmpty()) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonNegative(Integer value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value < 0) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonNegative(Long value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value < 0) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonNegative(Float value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value < 0) {
            throw exceptionSupplier.get();
        }
    }

    static void requireNonNegative(Double value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value < 0) {
            throw exceptionSupplier.get();
        }
    }

    static void requirePositive(Integer value, Supplier<ValidateException> exceptionSupplier) throws ValidateException {
        if (value == null || value <= 0) {
            throw exceptionSupplier.get();
        }
    }

    @Override
    default T apply(T t) {
        return validate(t);
    }

    T validate(T t) throws ValidateException;

}
