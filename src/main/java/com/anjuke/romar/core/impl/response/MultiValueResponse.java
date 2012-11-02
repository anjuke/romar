package com.anjuke.romar.core.impl.response;

import java.util.List;

import com.anjuke.romar.core.RomarResponse;

public class MultiValueResponse implements RomarResponse{

    private final List<?> _values;

    public MultiValueResponse(List<?> values) {
        super();
        _values = values;
    }

    public List<?> getValues() {
        return _values;
    }
}
