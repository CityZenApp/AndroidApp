package com.cityzen.cityzen.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Valdio Veliu on 06/05/2017.
 */

public class DeviceLocationData implements Parcelable {
    private double latitude;
    private double longitude;
    private String locality;//city name
    private String countryName;

    public DeviceLocationData(double latitude, double longitude, String locality, String countryName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.countryName = countryName;
    }

    protected DeviceLocationData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        locality = in.readString();
        countryName = in.readString();
    }

    public static final Creator<DeviceLocationData> CREATOR = new Creator<DeviceLocationData>() {
        @Override
        public DeviceLocationData createFromParcel(Parcel in) {
            return new DeviceLocationData(in);
        }

        @Override
        public DeviceLocationData[] newArray(int size) {
            return new DeviceLocationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(locality);
        parcel.writeString(countryName);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
