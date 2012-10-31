package com.anjuke.romar.http.rest.bean;

public class RecommendBean {
    private long _item;
    private float _value;

    public long getItem() {
        return _item;
    }

    public void setItem(long item) {
        _item = item;
    }

    public float getValue() {
        return _value;
    }

    public void setValue(float value) {
        _value = value;
    }

    @Override
    public String toString() {
        return "RecommendBean [_item=" + _item + ", _value=" + _value + "]";
    }

}
