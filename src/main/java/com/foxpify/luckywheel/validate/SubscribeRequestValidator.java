package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ErrorCode;
import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.request.SubscribeRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.regex.Pattern;

import static com.foxpify.luckywheel.validate.Validator.*;

@Singleton
public class SubscribeRequestValidator implements Validator<SubscribeRequest> {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
    @Inject
    public SubscribeRequestValidator() {
    }

    @Override
    public SubscribeRequest validate(SubscribeRequest subscribeRequest) throws ValidateException {
        requireNonEmpty(subscribeRequest.getFullName(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Subscribe request's fullName must not empty"));
        requireNonEmpty(subscribeRequest.getEmail(), () -> new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Subscribe request's email must not empty"));
        if (!emailPattern.matcher(subscribeRequest.getEmail()).matches()) {
            throw new ValidateException(ErrorCode.REQUIRED_PARAMETERS_MISSING_OR_INVALID, "Subscribe request's email is invalid format");
        }
        return subscribeRequest;
    }
}
