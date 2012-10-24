package com.anjuke.romar.mahout.persistence;

import java.util.Iterator;

import org.apache.mahout.cf.taste.model.Preference;

public interface PreferenceIterator extends Iterator<Preference>{
    public PreferenceType getType();
}
