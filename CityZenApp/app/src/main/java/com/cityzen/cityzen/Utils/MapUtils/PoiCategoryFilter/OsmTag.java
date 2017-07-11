package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class OsmTag {
    private String key;
    private String value;

    public OsmTag(String key, String value) {
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
