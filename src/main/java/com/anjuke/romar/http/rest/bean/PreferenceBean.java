package com.anjuke.romar.http.rest.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PreferenceBean {
    private long _user;
    private long _item;
    private float _value;

    public long getUser() {
        return _user;
    }

    public void setUser(long user) {
        _user = user;
    }

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

}
