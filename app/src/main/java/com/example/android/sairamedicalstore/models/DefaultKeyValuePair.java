package com.example.android.sairamedicalstore.models;

/**
 * Created by chandan on 16-08-2017.
 */

public class DefaultKeyValuePair {
    String key, value;

    public DefaultKeyValuePair() {
    }

    public DefaultKeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
