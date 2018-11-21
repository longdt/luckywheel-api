package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.entity.Slice;
import static com.foxpify.luckywheel.validate.Validator.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SliceValidator implements Validator<Slice> {

    @Inject
    public SliceValidator() {
    }

    @Override
    public Slice validate(Slice slice) throws ValidateException {
        requireNonEmpty(slice.getLabel(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Missing slice name"));
        Float prob = slice.getProbability();
        if (slice.getDiscountCode() != null && !slice.getDiscountCode().isEmpty() && prob == null) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Slice: " + slice.getLabel() + " must has prize probability in range [0, 1]");
        }
        if (prob != null && (prob < 0 || prob > 1)) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Slice: " + slice.getLabel() + " must has probability in range [0, 1]");
        }
        return slice;
    }
}
