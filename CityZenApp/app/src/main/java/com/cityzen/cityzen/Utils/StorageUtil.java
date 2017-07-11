package com.cityzen.cityzen.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Models.ParcelablePOI;

import java.util.ArrayList;
import java.lang.reflect.Type;

/**
 * Created by Valdio Veliu on 18/04/2017.
 */

public class StorageUtil {

    private SharedPreferences preferences;
    private Context context;

    private static final String FAVORITES = "com.cityzen.cityzen.FAVORITE_POIs";
    private static final String LOCATION = "com.cityzen.cityzen.LOCATION";

    public StorageUtil(Context context) {
        this.context = context;

    }

    public SharedPreferences oauthStorage() {
        return context.getSharedPreferences("OAUTH_OSM", Context.MODE_PRIVATE);
    }

    /*********************************************************************************************/


    /**********************Methods to store and retrieve the last known location of the device**********************/
    public void saveLastKnownLocation(DeviceLocationData location) {
        if (location == null) return;
        preferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("LATITUDE", String.valueOf(location.getLatitude()));
        editor.putString("LONGITUDE", String.valueOf(location.getLongitude()));
        editor.putString("LOCALITY", String.valueOf(location.getLocality()));
        editor.putString("COUNTRY_NAME", String.valueOf(location.getCountryName()));
        editor.apply();
    }

    public DeviceLocationData getLastKnownLocation() {
        preferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        String lat = preferences.getString("LATITUDE", null);
        String lon = preferences.getString("LONGITUDE", null);
        String locality = preferences.getString("LOCALITY", null);
        String countryName = preferences.getString("COUNTRY_NAME", null);
        if (lat == null || lon == null) return null; //latitude & longitude are required
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        if (locality == null) locality = "";
        if (countryName == null) countryName = "";
        return new DeviceLocationData(latitude, longitude, locality, countryName);
    }

    /****************************Methods used to operate one FAVORITE POIs******************************************/

    public void saveToFavoritesPOI(ParcelablePOI poi) {
        if (poi == null) return;
        preferences = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ArrayList<ParcelablePOI> previouslyStored = getFavoritePOIs();
        if (previouslyStored == null) {
            //No previous data
            previouslyStored = new ArrayList<>();
        }
        previouslyStored.add(0, poi);//add always new POIs to first position
        //Store FAVORITES
        Gson gson = new Gson();
        String json = gson.toJson(previouslyStored);
        editor.putString("FAVORITES", json);
        editor.apply();
    }

    public ArrayList<ParcelablePOI> getFavoritePOIs() {
        preferences = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        String pois;
        //Get favorites that are previously stored
        if ((pois = preferences.getString("FAVORITES", null)) == null) return null;
        if (pois.equals("")) return null;//no favorites stored yet

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ParcelablePOI>>() {
        }.getType();
        return gson.fromJson(pois, type);
    }

    public boolean isFavorite(ParcelablePOI poi) {
        ArrayList<ParcelablePOI> previouslyStored = getFavoritePOIs();
        if (previouslyStored == null) return false;
        for (ParcelablePOI parcelablePOI : previouslyStored) {
            if (parcelablePOI.getId() == poi.getId())
                return true;
        }
        return false;
    }

    public void deleteFromFavorites(ParcelablePOI poi) {
        ArrayList<ParcelablePOI> previouslyStored = getFavoritePOIs();
        if (previouslyStored == null) return;
        for (int i = 0; i < previouslyStored.size(); i++) {
            if (previouslyStored.get(i).getId() == poi.getId()) {
                previouslyStored.remove(i);
                preferences = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                //clear existing data
                editor.clear();
                editor.apply();
                //Store new FAVORITES Array
                Gson gson = new Gson();
                String json = gson.toJson(previouslyStored);
                editor.putString("FAVORITES", json);
                editor.apply();
            }
        }
    }
    /**********************************************End of {@link StorageUtil}***********************************************/
}
