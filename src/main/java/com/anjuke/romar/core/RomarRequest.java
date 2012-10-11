package com.anjuke.romar.core;

public interface RomarRequest {
    public String getPath();
    public long getUserId();
    public long getItemId();
    public float getPreference();
}
