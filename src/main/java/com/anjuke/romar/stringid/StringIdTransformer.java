package com.anjuke.romar.stringid;

public interface StringIdTransformer {
    long transform(String value);
    String transform(long value);
}
