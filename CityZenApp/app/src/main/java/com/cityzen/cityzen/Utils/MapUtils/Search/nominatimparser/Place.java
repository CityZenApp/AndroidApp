package com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser;

import android.content.Context;
import android.os.Parcel;

import java.util.HashMap;
import java.util.Map;

public class Place extends Adress {
    private long place_id, osm_id;
    private float importance;
    private String license, osm_type, display_name, entityClass, type;
    private BoundingBox boundingBox;
    private Map<String, String> tags = new HashMap<>();

    public Place(long place_id, long osm_id, double lat, double lon, float importance, String license, String osm_type, String display_name, String entityClass, String type, BoundingBox boundingBox, Map<String, String> tags) {
        super(display_name, android.R.mipmap.sym_def_app_icon, lat, lon);
        this.place_id = place_id;
        this.osm_id = osm_id;
        this.importance = importance;
        this.license = license;
        this.osm_type = osm_type;
        this.display_name = display_name;
        this.entityClass = entityClass;
        this.type = type;
        this.boundingBox = boundingBox;
        this.tags = tags;
    }

    public Place(Parcel in) {
        super(in);
        place_id = in.readLong();
        osm_id = in.readLong();
        importance = in.readFloat();
        license = in.readString();
        osm_type = in.readString();
        display_name = in.readString();
        entityClass = in.readString();
        type = in.readString();
    }

    @Override
    public String getName(Context c) {
        String[] split = display_name.split(",");
        return split[0] + "," + split[1];
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public long getPlaceId() {
        return place_id;
    }

    public long getOsmId() {
        return osm_id;
    }

    public double getLongitude() {
        return lon;
    }

    public double getLatitude() {
        return lat;
    }

    public float getImportance() {
        return importance;
    }

    public String getLicense() {
        return license;
    }

    public String getOsmType() {
        return osm_type;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return display_name;
    }

    @Override
    public String toString() {
        return display_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(place_id);
        dest.writeLong(osm_id);
        dest.writeFloat(importance);
        dest.writeString(license);
        dest.writeString(osm_type);
        dest.writeString(display_name);
        dest.writeString(entityClass);
        dest.writeString(license);
        dest.writeString(type);
    }

    public static final Creator CREATOR =
            new Creator() {
                public Place createFromParcel(Parcel in) {
                    return new Place(in);
                }

                public Place[] newArray(int size) {
                    return new Place[size];
                }
            };
}
