package com.foxpify.luckywheel.validate;

import com.foxpify.luckywheel.exception.ValidateException;
import com.foxpify.luckywheel.model.request.SubscribeRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SubscribeRequestValidator implements Validator<SubscribeRequest> {

    @Inject
    public SubscribeRequestValidator() {
    }

    @Override
    public SubscribeRequest validate(SubscribeRequest subscriber) throws ValidateException {
        return null;
    }
}
