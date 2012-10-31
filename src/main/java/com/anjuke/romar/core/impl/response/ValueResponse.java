package com.anjuke.romar.core.impl.response;

import com.anjuke.romar.core.RomarResponse;

public class ValueResponse implements RomarResponse {
    private final float _value;

    public ValueResponse(float value) {
        super();
        _value = value;
    }

    public float getValue() {
        return _value;
    }


}
