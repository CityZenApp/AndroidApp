package com.cityzen.cityzen.Fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.Development.AppToast;
import com.cityzen.cityzen.Utils.MapUtils.MapUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils.ParcelablePoiResponseListener;
import com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils.ReverseGeocodingTask;
import com.cityzen.cityzen.Utils.MapUtils.RouteUtils.PolylineCallback;
import com.cityzen.cityzen.Utils.MapUtils.RouteUtils.RouteCreationTask;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MapFragment extends Fragment implements MapEventsReceiver {

    private MapView map;
    private IMapController mController;
    private boolean addPoiMarker = false;
    private Marker createPoiMarker = null;
    private LinearLayout mapPoiCreationFormContainer;

    private Marker locationMarker;
    private Marker routeDestinationMarker;
    private Polyline existingRoute;
    private Timer routeUpdateTimer;
    private ParcelablePOI destinationPoi;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // deactivate map
        map.onDetach();
        //stop the redraw route timer if it exists
        killRouteTimer();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapSetup();
        viewSetup();

        if (map != null && (existingRoute = ((MainActivity) getActivity()).getMapRoute()) != null && ((MainActivity) getActivity()).getRouteMarker() != null) {
            //load routes if any previously exit
            map.getOverlays().add(existingRoute);
            Marker marker = ((MainActivity) getActivity()).getRouteMarker();
            routeDestinationMarker = MapUtils.addMarker(getActivity(), map, marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
            map.invalidate();

            ((MainActivity) getActivity()).toggleFabPoiDetailsAfterRoute();
            //restart the route redraw timer if there is a route to some destination
            overrideRouteOnLocationChange();
        }
    }

    private void viewSetup() {
        TextView osmCopyright = (TextView) getActivity().findViewById(R.id.osmCopyright);
        osmCopyright.setText(Html.fromHtml(getString(R.string.osm_copyright)));
        mapPoiCreationFormContainer = (LinearLayout) getActivity().findViewById(R.id.mapPoiCreationFormContainer);
        Button mapCancelPoiCreation = (Button) getActivity().findViewById(R.id.mapCancelPoiCreation);
        Button mapCreateNewPoi = (Button) getActivity().findViewById(R.id.mapCreateNewPoi);
        mapCancelPoiCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableCreatePoi();
            }
        });
        mapCreateNewPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createPoiMarker != null) {
                    //check authentication before creating
                    if (!((MainActivity) getActivity()).checkAuthentication()) return;
                    ((MainActivity) getActivity()).hideNavigation();

                    //hide crete poi data
                    try {
                        mapPoiCreationFormContainer.setVisibility(View.GONE);
                        addPoiMarker = false;
                        if (createPoiMarker == null) return;
                        if (createPoiMarker.isInfoWindowShown())
                            createPoiMarker.closeInfoWindow();
                        MapUtils.deleteMarker(createPoiMarker, map);
                    } catch (Exception e) {
                    }
                    openCreatePoiFragment(createPoiMarker);
                } else {
                    new AppToast(getActivity()).toast(getString(R.string.add_a_new_marker_on_the_map));
                }
            }
        });
    }

    public void openCreatePoiFragment(Marker marker) {
        if (marker == null) return;
        Fragment fragment = CreatePoiFragment.newInstance(marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "CreatePoiFragment");// give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    private void mapSetup() {
        map = (MapView) getActivity().findViewById(R.id.map);  // create basic map
        mController = map.getController(); // get map controller
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);

        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        //attach listeners
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay);
        setupMyLocation();
    }


    /*********************************************************************************************/
    /**
     * Clear the map form routes and remove create POI Flags
     */
    public void clearMapDisplay() {
        //disable create POI FLAGS
        disableCreatePoi();
        //clear routes
        killRouteTimer();
        //clear existingRoute
        if (existingRoute != null)
            map.getOverlays().remove(existingRoute);
        //clear routeDestinationMarker
        if (routeDestinationMarker != null)
            map.getOverlays().remove(routeDestinationMarker);
        //clear data from MainActivity
        ((MainActivity) getActivity()).clearRoute();
    }
    /*********************************************************************************************/

    /**
     * Show location marker for user
     */
    public void setupMyLocation() {
        DeviceLocationData location = ((MainActivity) getActivity()).getLastKnownLocation();
        if (location != null && map != null) {
            if (locationMarker == null) {
                locationMarker = MapUtils.myLocation(getActivity(), map, location.getLatitude(), location.getLongitude());
                //center to my location
                GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());
                map.getController().setCenter(position);
                map.getController().setZoom(16);
            } else {
                //center to my location
                GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());
                locationMarker.setPosition(position);
            }
        }
    }

    public void showMyLocation() {
        if (locationMarker != null && map != null) {
            //center to my location
            map.getController().setCenter(locationMarker.getPosition());
            map.getController().setZoom(16);
        }
    }

    /****************************************Create Point of Interest******************************************/

    public void enableCreatePoi() {
        addPoiMarker = true;

        //kill the reroute timer
        killRouteTimer();
        //clear data from MainActivity
        ((MainActivity) getActivity()).clearRoute();
        //clear existing routes form map and destination markers
        if (routeDestinationMarker != null)
            map.getOverlays().remove(routeDestinationMarker);
        if (existingRoute != null)
            map.getOverlays().remove(existingRoute);
    }

    private void disableCreatePoi() {
        mapPoiCreationFormContainer.setVisibility(View.GONE);
        addPoiMarker = false;
        if (createPoiMarker == null) return;
        if (createPoiMarker.isInfoWindowShown())
            createPoiMarker.closeInfoWindow();
        MapUtils.deleteMarker(createPoiMarker, map);
        createPoiMarker = null;
    }

    /**************************************Routes getter methods********************************************/
    public void showDirectionsToPoi(ParcelablePOI poi) {
        destinationPoi = poi;
        //clear existing routes form map and destination markers
        if (routeDestinationMarker != null)
            map.getOverlays().remove(routeDestinationMarker);
        if (existingRoute != null)
            map.getOverlays().remove(existingRoute);
        //clear data from MainActivity
        ((MainActivity) getActivity()).clearRoute();


        DeviceLocationData location = ((MainActivity) getActivity()).getLastKnownLocation();
        if (location != null) {
            createRoute(poi, location.getLatitude(), location.getLongitude(), poi.getLatitude(), poi.getLongitude());
        }
    }

    /**
     * Create route on map
     *
     * @param fromLatitude  Latitude to start drawing, users location
     * @param fromLongitude Longitude to start drawing, users location
     * @param toLatitude    Latitude to end drawing
     * @param toLongitude   Longitude to end drawing
     */
    private void createRoute(double fromLatitude, double fromLongitude, final double toLatitude, final double toLongitude) {
        new RouteCreationTask(getActivity(), map, fromLatitude, fromLongitude, toLatitude, toLongitude, new PolylineCallback() {
            @Override
            public void onPolylineCreated(Polyline polyline) {
                existingRoute = polyline;

                //clear previously added marker
                if (routeDestinationMarker != null)
                    map.getOverlays().remove(routeDestinationMarker);
                //add destination marker if
                routeDestinationMarker = MapUtils.addMarker(getActivity(), map, toLatitude, toLongitude);
                if (getActivity() != null)
                    ((MainActivity) getActivity()).routeAddedOnMap(routeDestinationMarker, existingRoute);

                //start task to redraw the route ever 30 seconds, if no previous timer is set ont this route
                if (routeUpdateTimer == null)
                    overrideRouteOnLocationChange();
            }

            @Override
            public void onFailure() {
                new AppToast(getActivity()).centerViewToast(getString(R.string.osm_routing_unavailable));
            }
        }).execute();
    }


    private void createRoute(final ParcelablePOI poi, double fromLatitude, double fromLongitude, final double toLatitude, final double toLongitude) {
        new RouteCreationTask(getActivity(), map, fromLatitude, fromLongitude, toLatitude, toLongitude, new PolylineCallback() {
            @Override
            public void onPolylineCreated(Polyline polyline) {
                existingRoute = polyline;

                //clear previously added marker
                if (routeDestinationMarker != null)
                    map.getOverlays().remove(routeDestinationMarker);
                //add destination marker if
                routeDestinationMarker = MapUtils.addMarker(getActivity(), map, toLatitude, toLongitude);
                ((MainActivity) getActivity()).routeAddedOnMap(poi, routeDestinationMarker, existingRoute);

                //start task to redraw the route ever 30 seconds, if no previous timer is set ont this route
                if (routeUpdateTimer == null)
                    overrideRouteOnLocationChange();
            }

            @Override
            public void onFailure() {
                new AppToast(getActivity()).centerViewToast(getString(R.string.osm_routing_unavailable));
            }
        }).execute();
    }

    /**
     * Redraw the route form source to destination
     */
    private void overrideRouteOnLocationChange() {
        if (existingRoute != null) {
            routeUpdateTimer = new Timer();
            routeUpdateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //Redraw route every 15 seconds when the user moves on the map
                    //clear existing Route
                    if (existingRoute != null && map != null)
                        map.getOverlays().remove(existingRoute);
                    //clear data from MainActivity
                    ((MainActivity) getActivity()).clearRoutePolyline();

                    Marker destinationMarker = ((MainActivity) getActivity()).getRouteMarker();
                    DeviceLocationData location = ((MainActivity) getActivity()).getLastKnownLocation();
                    if (location != null && destinationMarker != null)
                        createRoute(location.getLatitude(), location.getLongitude(), destinationMarker.getPosition().getLatitude(), destinationMarker.getPosition().getLongitude());

                }
            }, 5000, 15000);// First time start after 5 seconds and repeat after 15 seconds
        }
    }

    /**
     * Stop the rerouting timer
     */
    private void killRouteTimer() {
        if (routeUpdateTimer != null) {
            routeUpdateTimer.cancel();
            routeUpdateTimer = null;
        }
    }

    /**************************Reverse Geocoding methods, get POI info on map click****************************************/
    private void getPoiAtLocation(double lat, double lon) {
        new ReverseGeocodingTask(getActivity(), lat, lon, ((MainActivity) getActivity()).getOsm(), new ParcelablePoiResponseListener() {
            @Override
            public void onPoiReceived(ParcelablePOI poi) {
                openDetailedInfo(poi);
            }

            @Override
            public void onFailure() {

            }
        }).execute();
    }

    private void openDetailedInfo(ParcelablePOI poi) {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            PoiDetailsFragment dialogFragment = PoiDetailsFragment.newInstance(poi);
            dialogFragment.show(fm, "PoiDetailsFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*****************************************Map event listeners****************************************************/
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        if (addPoiMarker) {
            if (createPoiMarker == null)
                createPoiMarker = MapUtils.addMarker(getActivity(), map, p);
            else {//move to location
                if (createPoiMarker.isInfoWindowShown())
                    createPoiMarker.closeInfoWindow();
                createPoiMarker.setPosition(p);
            }
            mapPoiCreationFormContainer.setVisibility(View.VISIBLE);
        } else
            getPoiAtLocation(p.getLatitude(), p.getLongitude());
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        disableCreatePoi();//disable poi creation if it is enabled
        DeviceLocationData location = ((MainActivity) getActivity()).getLastKnownLocation();
        if (location != null) {
            //clear existingRoute
            if (existingRoute != null) {
                map.getOverlays().remove(existingRoute);
                killRouteTimer();//kill this route re-render timer
            }
            //clear routeDestinationMarker
            if (routeDestinationMarker != null)
                map.getOverlays().remove(routeDestinationMarker);
            //clear data from MainActivity
            ((MainActivity) getActivity()).clearRoute();

            createRoute(location.getLatitude(), location.getLongitude(), p.getLatitude(), p.getLongitude());
        }
        return false;
    }


    /*********************************************End of {@link MapFragment}*****************************************/
}
