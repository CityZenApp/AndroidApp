package com.cityzen.cityzen.Utils.MapUtils;

import android.content.Context;

import com.cityzen.cityzen.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Map;

import info.metadude.java.library.overpass.models.Element;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class MapUtils {

    /**
     * Add new {@link Marker} to {@link MapView}
     *
     * @param context  The context to use. Usually your {@link android.app.Application} or {@link android.app.Activity} object.
     * @param map      {@link MapView} to add marker on
     * @param position Location of the marker
     */
    public static Marker addMarker(Context context, MapView map, GeoPoint position) {
        Marker marker = new Marker(map);
        marker.setPosition(position);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_location));
        marker.setTitle("Latitude: " + position.getLatitude() + "\n" + "Longitude: " + position.getLongitude());
        marker.setPanToView(true);
        marker.setDraggable(true);
        map.getOverlays().add(marker);
        map.invalidate();
        return marker;
    }

    /**
     * Delete a marker from map
     *
     * @param marker {@link Marker} to be deleted
     * @param map    The {@link MapView} which holds the marker
     */
    public static void deleteMarker(Marker marker, MapView map) {
        map.getOverlays().remove(marker);
        map.invalidate();
    }


    public static void addMarker(Context context, MapView map, Element element) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(element.lat, element.lon));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_location));

        marker.setTitle(String.valueOf(""));
        for (Map.Entry<String, String> tag : element.tags.entrySet()) {
            if (tag.getKey().equals("name")) {
                marker.setTitle(String.valueOf(tag.getValue()));
                break;
            }
        }
        if (marker.getTitle().equals(""))
            marker.setTitle(String.valueOf(element.id));

    }

    public static void addMarker(Context context, MapView map, double latitude, double longitude, String markerTitle) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_location));
        marker.setTitle(markerTitle);
    }

    public static Marker addMarker(Context context, MapView map, double latitude, double longitude) {
        if (map == null || context == null) return null;
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_location));
        marker.setInfoWindow(null);
        map.getOverlays().add(marker);
        map.invalidate();
        return marker;
    }

    public static Marker myLocation(Context context, MapView map, double latitude, double longitude) {
        if (map == null || context == null) return null;
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(context.getResources().getDrawable(R.drawable.ic_gps_location));
        marker.setInfoWindow(null);
        map.getOverlays().add(marker);
        map.invalidate();
        return marker;
    }
}
