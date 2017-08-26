package com.cityzen.cityzen.Utils.MapUtils.RouteUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.cityzen.cityzen.R;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import okhttp3.Route;


/**
 * Created by Valdio Veliu on 03/05/2017.
 */

public class RouteCreationTask extends AsyncTask<Void, Void, Void> {

    private MapView map;
    private Context context;
    private double fromLatitude;
    private double fromLongitude;
    private double toLatitude;
    private double toLongitude;
    private PolylineCallback callback;

    private Polyline roadOverlay;
    private Road road;

    public RouteCreationTask(Context context, MapView map, double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, PolylineCallback callback) {
        this.map = map;
        this.context = context;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getRoadBetweenLocations();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        boolean roadBuiltSuccesfully = createPolyline();
        if (!roadBuiltSuccesfully) callback.onFailure();
        else {
            if (roadOverlay != null)
                callback.onPolylineCreated(roadOverlay);
            else
                callback.onFailure();
        }
    }

    private void getRoadBetweenLocations() {
        RoadManager roadManager = new OSRMRoadManager(context);
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(new GeoPoint(fromLatitude, fromLongitude));
        GeoPoint endPoint = new GeoPoint(toLatitude, toLongitude);
        waypoints.add(endPoint);
        road = roadManager.getRoad(waypoints);
    }

    private boolean createPolyline() {
        if (map == null || road.mStatus == Road.STATUS_INVALID || road.mStatus == Road.STATUS_TECHNICAL_ISSUE)
            return false;// some error with the road
        roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setColor(context.getResources().getColor(R.color.colorAccent));
        roadOverlay.setWidth(16);
        roadOverlay.setGeodesic(true);

//        Drawable nodeIcon = context.getResources().getDrawable(R.drawable.ic_location);
//        for (int i = 0; i < road.mNodes.size(); i++) {
//            RoadNode node = road.mNodes.get(i);
//            Marker nodeMarker = new Marker(map);
//            nodeMarker.setPosition(node.mLocation);
//            nodeMarker.setIcon(nodeIcon);
//            nodeMarker.setTitle("Step " + i);
//            map.getOverlays().add(nodeMarker);
//            nodeMarker.setSnippet(node.mInstructions);
//            nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
//            Drawable icon = context.getResources().getDrawable(R.drawable.ic_filter_selected);
//            nodeMarker.setImage(icon);
//        }
        map.getOverlays().add(roadOverlay);
        map.invalidate();
        return true;
    }

}
