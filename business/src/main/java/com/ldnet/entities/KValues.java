package com.ldnet.entities;

/**
 * Created by Murray on 2015/9/9.
 */
public class KValues {
    public KValues() {
    }

    public KValues(String key, String value) {
        Key = key;
        Value = value;
    }

    public String Key;
    public String Value;

    @Override
    public String toString() {
        return Value;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
