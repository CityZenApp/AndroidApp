package com.cityzen.cityzen.Models;

/**
 * Created by Valdio Veliu on 14/05/2017.
 */

public class OsmFeature {
    private String key;
    private String value;

    public OsmFeature(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
