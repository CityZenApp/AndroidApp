package com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cityzen.cityzen.ApplicationConstants;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.Development.AppLog;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.data.Node;

/**
 * Created by Valdio Veliu on 03/05/2017.
 */

/**
 * Task returns a OSM node if it finds one
 */
public class ReverseGeocodingTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private double latitude;
    private double longitude;
    private OsmConnection connection;
    private ParcelablePoiResponseListener listener;
    private Address reverseGeocodedAddress = null;
    private ParcelablePOI poi = null;

    public ReverseGeocodingTask(Context context, double latitude, double longitude, OsmConnection connection, ParcelablePoiResponseListener listener) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.connection = connection;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        GeocoderNominatim nominatim = new GeocoderNominatim(ApplicationConstants.USER_AGENT);
        try {
            List<Address> addresses = nominatim.getFromLocation(latitude, longitude, 1);//only get one location
            if (addresses != null && addresses.size() > 0) {
                reverseGeocodedAddress = addresses.get(0);
                Bundle bundle = addresses.get(0).getExtras();
                String osm_type = bundle.getString("osm_type");
                AppLog.log(addresses.get(0));
                //only osm nodes are filtered though
//                if (osm_type.equals("node")) {
                double nodeLat = reverseGeocodedAddress.getLatitude();
                double nodeLon = reverseGeocodedAddress.getLongitude();
                //The geocoding is considered as valid if the node location
                //is under the distance 4 meters form the provided location (user click location on map)
                AppLog.log(distance(latitude, nodeLat, longitude, nodeLon, 0, 0));
                if (distance(latitude, nodeLat, longitude, nodeLon, 0, 0) < 4) {
                    long osm_id = bundle.getLong("osm_id");
                    getNode(connection, osm_id);
                }
//                } else {
//                    //Do noting
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    private static double distance(double lat1, double lat2, double lon1,
                                   double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private void getNode(OsmConnection connection, long nodeId) {
        new NodeInfoTask(connection, nodeId, new NodeResponseListener() {
            @Override
            public void onPoiReceived(Node node) {
                if (reverseGeocodedAddress != null && node != null) {
                    Map<String, String> tags = node.getTags();
                    String poiClassName = "";
                    String poiClassType = "";

                    if (tags != null)
                        for (Map.Entry<String, String> tag : tags.entrySet()) {
                            /**
                             * If added more tags to filter in {@link com.openstreetmap.opencity.opencity.Utils.MapUtils.PoiCategoryFilter.FilterCategory} add the new tags in the if statement
                             */
                            if (tag.getKey().equals("amenity") || tag.getKey().equals("tourism") || tag.getKey().equals("historic") || tag.getKey().equals("building") || tag.getKey().equals("shop")) {
                                poiClassName = tag.getKey();
                                poiClassType = tag.getValue();
                            }
                        }
                    Bundle bundle = reverseGeocodedAddress.getExtras();
                    String display_name = bundle.getString("display_name");
                    String poiName = "";
                    if (display_name.contains(","))
                        poiName = display_name.substring(0, display_name.indexOf(","));
                    else
                        poiName = display_name;
                    poi = new ParcelablePOI(
                            bundle.getLong("osm_id"),
                            poiName,
                            display_name,
                            bundle.getString("osm_type"), //always node
                            poiClassName,
                            poiClassType,
                            reverseGeocodedAddress.getLatitude(),
                            reverseGeocodedAddress.getLongitude(),
                            node.getTags());

                    listener.onPoiReceived(poi);
                }
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        }).execute();
    }
}
