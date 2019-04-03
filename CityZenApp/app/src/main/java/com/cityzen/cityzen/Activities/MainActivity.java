package com.cityzen.cityzen.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cityzen.cityzen.Fragments.CategoriesFragment;
import com.cityzen.cityzen.Fragments.EditPoiFragment;
import com.cityzen.cityzen.Fragments.FavoritesFragment;
import com.cityzen.cityzen.Fragments.MapFragment;
import com.cityzen.cityzen.Fragments.PoiDetailsFragment;
import com.cityzen.cityzen.Fragments.PoiListFragment;
import com.cityzen.cityzen.Fragments.SearchFragment;
import com.cityzen.cityzen.Fragments.SettingsFragment;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.OsmModule;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.Development.AppToast;
import com.cityzen.cityzen.Utils.DeviceUtils.DeviceUtils;
import com.cityzen.cityzen.Utils.RecyclerView.CategoryDisplayConfig;
import com.cityzen.cityzen.Utils.StorageUtil;
import com.cityzen.cityzen.oauth.OAuth;
import com.cityzen.cityzen.oauth.OAuthComponent;
import com.cityzen.cityzen.oauth.OAuthWebViewDialogFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.westnordost.osmapi.OsmConnection;
import info.metadude.java.library.overpass.models.Element;
import oauth.signpost.OAuthConsumer;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements OAuthWebViewDialogFragment.OAuthListener,
        OAuthComponent.Listener, EasyPermissions.PermissionCallbacks {

    public static final String CHARSET = "UTF-8";
    private static final int DEFAULT_TIMEOUT = 45 * 1000;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 101;
    private static final int LOCATION_PERMISSION = 102;

    private List<Element> poiElementsOfCategory;

    OAuthComponent oAuthComponent;
    SharedPreferences prefs;
    OsmConnection osm;

    //keep tract of the color of the status bar
    private int statusBatColor;

    private BottomNavigationViewEx navigation;
    private FloatingActionMenu floatingActionButton;

    private TextView mTextMessage;
    private MapView mapView;
    private String TAG = "OPEN_STREET_MAP";
    private LocationManager locationManager;
    private LocationListener locationListener;
    //stores the location of the device
    private DeviceLocationData lastKnownLocation;
    private Marker routeMarker;
    private Polyline mapRoute;
    private ParcelablePOI destinationPoi;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabPoiDetailsAfterRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        //load last known location from SharedPreferences
        lastKnownLocation = new StorageUtil(this).getLastKnownLocation();

        if (lastKnownLocation == null) {
            registerReceiver(locationEnabledReceiver, new IntentFilter("LOCATION_ENABLED"));
        }

        //request permissions
        requestWriteExternalStoragePermission();

        getStatusBatColor();
        updateStatusBarColor();
        viewSetup();

        setupOsmConnection();

//        AppLog.log("OAUTH");
//        AppLog.log(osm.getUserAgent());
//        AppLog.log(osm.getApiUrl());
//        AppLog.log(osm.getOAuth().getConsumerKey());
//        AppLog.log(osm.getOAuth().getConsumerSecret());
//        AppLog.log(osm.getOAuth().getRequestParameters());
//        logUserInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigation.setVisibility(View.VISIBLE);
    }


    public void updateStatusBarColor() {// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void revertStatusBarColor() {// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBatColor);
        }
    }

    private void getStatusBatColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            statusBatColor = window.getStatusBarColor();
        }
    }

    /**********************************Setup UI elements and their functionality***********************************/
    private void viewSetup() {
        fabPoiDetailsAfterRoute = findViewById(R.id.fabPoiDetailsAfterRoute);
        floatingActionButton = findViewById(R.id.mainFab);
        floatingActionButton.setClosedOnTouchOutside(true);

        navigation = findViewById(R.id.navigation);
        //setup bottom navigation
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_categories);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);

        FloatingActionButton fabClearMap = findViewById(R.id.fabClearMap);
        FloatingActionButton fabLocation = findViewById(R.id.fabLocation);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabSearch = findViewById(R.id.fabSearch);
        fabClearMap.setOnClickListener(clickListenerFAB);
        fabLocation.setOnClickListener(clickListenerFAB);
        fabAdd.setOnClickListener(clickListenerFAB);
        fabSearch.setOnClickListener(clickListenerFAB);
        fabPoiDetailsAfterRoute.setOnClickListener(clickListenerFAB);
    }

    /**
     * This method is used to toggle the FAB on the map, for displaying the {@link PoiDetailsFragment} of the POI we are routing to
     * Sometimes this method is called from a background thread, therefore its used the runOnUiThread method
     */
    public void toggleFabPoiDetailsAfterRoute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (navigation.getSelectedItemId() == R.id.navigation_map) {
                    if (destinationPoi != null && mapRoute != null && routeMarker != null)
                        fabPoiDetailsAfterRoute.show();
                    else
                        fabPoiDetailsAfterRoute.hide();
                } else {
                    fabPoiDetailsAfterRoute.hide();
                }
            }
        });
    }

    private View.OnClickListener clickListenerFAB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabPoiDetailsAfterRoute:
                    floatingActionButton.close(true);
                    //remove routes and add create POi markers form map
                    if (navigation.getSelectedItemId() == R.id.navigation_map) {
                        FragmentManager fm = getSupportFragmentManager();
                        PoiDetailsFragment dialogFragment = PoiDetailsFragment.newInstance(destinationPoi);
                        dialogFragment.show(fm, "PoiDetailsFragment");
                    }
                    break;
                case R.id.fabClearMap:
                    floatingActionButton.close(true);
                    //remove routes and add create POi markers form map
                    if (navigation.getSelectedItemId() == R.id.navigation_map) {
                        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
                        fragment.clearMapDisplay();
                    }
                    break;
                case R.id.fabLocation:
                    floatingActionButton.close(true);
                    showMyLocation();
                    break;
                case R.id.fabAdd:
                    floatingActionButton.close(true);
                    if (navigation.getSelectedItemId() == R.id.navigation_map) {
                        new AppToast(MainActivity.this).centerViewToast(getString(R.string.add_new_poi_toast));
                        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
                        fragment.enableCreatePoi();
                    }
                    break;
                case R.id.fabSearch:
                    floatingActionButton.close(true);
                    navigation.setSelectedItemId(R.id.navigation_search);
                    break;
            }
        }
    };

    /**
     * Method used to get the name of the logged in user
     *
     * @param getUserInfo
     */
    public void logUserInfo(final SettingsFragment.getUserInfo getUserInfo) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... voids) {
                try {
                    String username = OsmModule.userDao(osm).get(OsmModule.userDao(osm).getMine().id).displayName;
                    if (username != null)
                        getUserInfo.onResponse(username);
                    else
                        getUserInfo.onFailure();
                } catch (Exception e) {
                    getUserInfo.onFailure();
                }
                return null;
            }
        }.execute();
    }

    /*****************************************Bottom Navigation methods***********************************************/
    /**
     * Function used to refresh the favorites screen,
     * in the case that there are POIs being added or removed by favorites.
     */
    public void refreshAppNavigation() {
        if (navigation.getSelectedItemId() == R.id.navigation_favorites)
            navigation.setSelectedItemId(R.id.navigation_favorites);// refresh favorites fragment
    }

    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_categories:
                    fragment = CategoriesFragment.newInstance();
                    transaction.replace(R.id.content, fragment, "CategoriesFragment");
                    transaction.commit();
                    floatingActionButton.setVisibility(View.GONE);
                    fabPoiDetailsAfterRoute.hide();
                    return true;
                case R.id.navigation_map:
                    if (navigation.getSelectedItemId() != R.id.navigation_map) {
                        fragment = MapFragment.newInstance();
                        transaction.replace(R.id.content, fragment, "MapFragment");
                        transaction.commit();
                        floatingActionButton.setVisibility(View.VISIBLE);
//                        fabPoiDetailsAfterRoute.setVisibility(View.VISIBLE);
                    }
                    toggleFabPoiDetailsAfterRoute();

                    return true;
                case R.id.navigation_search:
                    fragment = SearchFragment.newInstance();
                    transaction.replace(R.id.content, fragment, "SearchFragment");
                    transaction.commit();
                    floatingActionButton.setVisibility(View.GONE);
                    fabPoiDetailsAfterRoute.hide();
                    return true;
                case R.id.navigation_favorites:
                    fragment = FavoritesFragment.newInstance();
                    transaction.replace(R.id.content, fragment, "FavoritesFragment");
                    transaction.commit();
                    floatingActionButton.setVisibility(View.GONE);
                    fabPoiDetailsAfterRoute.hide();
                    return true;
                case R.id.navigation_settings:
                    fragment = SettingsFragment.newInstance();
                    transaction.replace(R.id.content, fragment, "SettingsFragment");
                    transaction.commit();
                    floatingActionButton.setVisibility(View.GONE);
                    fabPoiDetailsAfterRoute.hide();
                    return true;
            }
            return false;
        }
    };

    public void hideNavigation() {
        navigation.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
    }

    public void showNavigation() {
        navigation.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    public void showNavigationNoFab() {
        navigation.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
    }

    /**********************Requesting Runtime permissions for Android M and newer devices*****************************/
    /**
     * Request runtime permissions method
     */
    private void requestWriteExternalStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //Up next .... request Location
            requestLocationPermission();
        } else {
            // Do not have permissions, request permissions
            EasyPermissions.requestPermissions(MainActivity.this,
                    getString(R.string.write_storage_request),
                    WRITE_EXTERNAL_STORAGE_PERMISSION,
                    permissions);
        }
    }

    private void requestLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //do nothing
            checkLocationServicesStatus();
        } else {
            // Do not have permissions, request permissions
            EasyPermissions.requestPermissions(MainActivity.this,
                    getString(R.string.location_request),
                    LOCATION_PERMISSION,
                    permissions);
        }
    }

    /**
     * Check if location services are enabled in the device
     */
    void checkLocationServicesStatus() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            getDeviceLocation();
        }
    }

    /**
     * If location services are disabled in the device,
     * build a dialog to open settings, to enable location
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_message)
                .setCancelable(false)
                .setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getDeviceLocation();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Get device location, needed form map functionality, category filter, and search POIs
     */
    private void getDeviceLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //get initial location state
//        if (getLastKnownLocation() == null) {
        Location initialLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (initialLocation != null) {
            saveLastKnownLocation(initialLocation);
            notifyMapLocationChanged();
        }
//        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (DeviceUtils.isGPSEnabled(MainActivity.this)) {
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&&
                                ActivityCompat.checkSelfPermission(MainActivity.this,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/) {
                            return;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            saveLastKnownLocation(location);
                            notifyMapLocationChanged();
                        }
                    }
                } else if (DeviceUtils.isInternetConnected(MainActivity.this)) {
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            saveLastKnownLocation(location);
                            notifyMapLocationChanged();
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    /**
     * Save last know device location on {@link SharedPreferences} and as a global variable in {@link MainActivity}
     *
     * @param location Location of the device
     */
    public void saveLastKnownLocation(Location location) {
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            DeviceLocationData deviceLocation =
                    new DeviceLocationData(
                            location.getLatitude(),
                            location.getLongitude(),
                            addresses.get(0).getLocality(),
                            addresses.get(0).getCountryName());
            new StorageUtil(MainActivity.this).saveLastKnownLocation(deviceLocation);
            lastKnownLocation = deviceLocation;
        } else {
            DeviceLocationData deviceLocation =
                    new DeviceLocationData(
                            location.getLatitude(),
                            location.getLongitude(),
                            "",
                            "");
            new StorageUtil(MainActivity.this).saveLastKnownLocation(deviceLocation);
            lastKnownLocation = deviceLocation;
        }
    }

    /**
     * Notify {@link MapFragment} when the location has changed
     */
    public void notifyMapLocationChanged() {
        if (navigation != null && navigation.getSelectedItemId() == R.id.navigation_map) {
            MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
            if (fragment != null)
                fragment.setupMyLocation();
        }
    }


    /**
     * Center map to my location, FAB my location click
     */
    private void showMyLocation() {
        if (navigation.getSelectedItemId() == R.id.navigation_map) {
            MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
            if (fragment != null)
                fragment.showMyLocation();
        }
    }

    /**
     * Get last get LastKnownLocation of device,
     * location is updated frequently if GPS is enabled
     *
     * @return {@link DeviceLocationData} object
     */
    public DeviceLocationData getLastKnownLocation() {
        return lastKnownLocation;
    }

    /**
     * Forward all permission requests to {@link EasyPermissions}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION) {
            //double check that write access is granted
            requestWriteExternalStoragePermission();
        } else if (requestCode == LOCATION_PERMISSION) {
            //double check that write access is granted
            requestLocationPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION) {
            new AppToast(this).longToast(R.string.write_storage_request_toast);

            //double check that write access is granted, this permission is needed
            requestWriteExternalStoragePermission();
            // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
            // This will display a dialog directing them to enable the permission in app settings.
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this).build().show();
                }
        } else if (requestCode == LOCATION_PERMISSION) {
            new AppToast(this).longToast(R.string.location_request_toast);

            //double check that write access is granted, this permission is needed
            requestLocationPermission();
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
            // This will display a dialog directing them to enable the permission in app settings.
            if (!EasyPermissions.hasPermissions(this, permissions))
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this).build().show();
                }
        }
    }

    /*********************************************************************************************/
    /**
     * Open {@link PoiListFragment}, connection with {@link CategoriesFragment}
     * Used variables:
     * 1. poiElementsOfCategory
     *******************************************************************************************/
    public void openCategoryList(CategoryDisplayConfig displayConfig, List<Element> elements) {
        poiElementsOfCategory = elements;
        Fragment fragment = PoiListFragment.newInstance(this.getString(displayConfig.title), displayConfig.id, displayConfig.color);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "PoiListFragment");// give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
        hideNavigation();//hide bottom navigation
    }

    public List<Element> getPoiElementsOfCategory() {
        return poiElementsOfCategory;
    }

    public void showLoadingScreen() {
        FrameLayout loaderContainer = findViewById(R.id.loaderContainer);
        loaderContainer.setVisibility(View.VISIBLE);
        hideNavigation();
    }

    public void hideLoadingScreen() {
        FrameLayout loaderContainer = (FrameLayout) findViewById(R.id.loaderContainer);
        loaderContainer.setVisibility(View.GONE);
    }

    public void hideLoadingScreenWithNavigation() {
        FrameLayout loaderContainer = (FrameLayout) findViewById(R.id.loaderContainer);
        loaderContainer.setVisibility(View.GONE);
        navigation.setVisibility(View.VISIBLE);
    }

    /**********************************************Edit POI***********************************************/
    /**
     * Open {@link EditPoiFragment} to add more info o POi
     *
     * @param poi {@link ParcelablePOI} to edit
     */
    public void editPoi(ParcelablePOI poi) {
        //check authentication before editing
        if (!checkAuthentication()) return;
        //close PoiListFragment if it is open
        PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager().findFragmentByTag("PoiListFragment");
        if (poiListFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(poiListFragment)
                    .commit();

        //if MapFragment is open close any routing
        MapFragment MapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
        if (MapFragment != null) {
            MapFragment.clearMapDisplay();
        }

        Fragment fragment = EditPoiFragment.newInstance(poi);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "EditPoiFragment");// give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
        hideNavigation();
    }
    /**********************************************Directions to POI***********************************************/
    /**
     * Show route directions to Poi form device location
     *
     * @param poi {@link ParcelablePOI} to show location to
     */
    public void showDirectionsToPoi(ParcelablePOI poi) {
        //close PoiListFragment if it is open
        PoiListFragment poiListFragment = (PoiListFragment) getSupportFragmentManager().findFragmentByTag("PoiListFragment");
        if (poiListFragment != null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(poiListFragment)
                    .commit();
        //open MapFragment
        if (navigation.getSelectedItemId() != R.id.navigation_map)
            navigation.setSelectedItemId(R.id.navigation_map);
        showNavigation();
        loadDirectionsIfPossible(poi);

    }

    /**
     * Closing {@link PoiListFragment} and opening the {@link MapFragment} requires time.
     * Can't build directions to POI before the process is complete.
     * The following timer tries to build the route every half a second if it can.
     *
     * @param poi {@link ParcelablePOI} to show location to
     */
    private void loadDirectionsIfPossible(final ParcelablePOI poi) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (navigation.getSelectedItemId() == R.id.navigation_map) {
                    MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("MapFragment");
                    if (fragment != null) {
                        fragment.showDirectionsToPoi(poi);
                        timer.cancel();//disable timer
                    }
                }
            }

        }, 0, 500);//Update every half a second
    }

    /**
     * Method save the routes of the map, directions to POI if any exist,
     * to be loaded again when the map is redrawn
     * This method will be called when a route is drawn on the map
     * to keep track of the route
     */
    public void routeAddedOnMap(Marker routeMarker, Polyline mapRoute) {
        this.routeMarker = routeMarker;
        this.mapRoute = mapRoute;
    }

    public void routeAddedOnMap(ParcelablePOI poi, Marker routeMarker, Polyline mapRoute) {
        this.destinationPoi = poi;
        this.routeMarker = routeMarker;
        this.mapRoute = mapRoute;
        toggleFabPoiDetailsAfterRoute();
    }

    public void clearRoute() {
        this.routeMarker = null;
        this.mapRoute = null;
        this.destinationPoi = null;
        toggleFabPoiDetailsAfterRoute();
    }

    public void clearRoutePolyline() {
        this.mapRoute = null;
    }

    public Marker getRouteMarker() {
        return routeMarker;
    }

    public Polyline getMapRoute() {
        return mapRoute;
    }

    public void setDestinationPoi(ParcelablePOI poi) {
        this.destinationPoi = poi;
    }

    /**********************************************Location settings enabled *****************************************/
    BroadcastReceiver locationEnabledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDeviceLocation();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(locationEnabledReceiver);
        } catch (Exception e) {
        }
    }


    /********************************************** OSM basic methods ***********************************************/

    private void setupOsmConnection() {
        prefs = new StorageUtil(this).oauthStorage();
        osm = OsmModule.osmConnection(OsmModule.oAuthConsumer(prefs));
        oAuthComponent = new OAuthComponent(prefs, this, OsmModule.userDao(osm), osm);
        oAuthComponent.setListener(this);

    }

    public OsmConnection getOsm() {
        return osm;
    }


    /**********************************************OSM authentication*********************************************/
    //in case of upload data to OSM call this function to authentication the user
    public boolean checkAuthentication() {
        // because the app should ask for permission even if there is nothing to upload right now
        if (!OAuth.isAuthorized(prefs)) {
            requestOAuthorized();
            return false;
        } else {
            return true;//user authenticated
        }
    }

    public void recheckAuthentication() {
        if (prefs != null)//clear previous authentication data, might be expired
            OAuth.deleteConsumer(prefs);
        checkAuthentication();
    }

    private void requestOAuthorized() {
        DialogInterface.OnClickListener onYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OAuthWebViewDialogFragment dlg = OAuthWebViewDialogFragment.create(
                        OAuth.createConsumer(), OAuth.createProvider());
                dlg.show(getFragmentManager(), OAuthWebViewDialogFragment.TAG);
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(R.string.confirmation_authorize_now)
                .setPositiveButton(android.R.string.ok, onYes)
                .setNegativeButton(R.string.later, null).show();
    }


    @Override
    public void onOAuthAuthorized(OAuthConsumer consumer, List<String> permissions) {
        oAuthComponent.onOAuthAuthorized(consumer, permissions);
    }

    @Override
    public void onOAuthCancelled() {
        oAuthComponent.onOAuthCancelled();
    }

    @Override
    public void onOAuthAuthorizationVerified() {
//        answersCounter.update();
        // now finally we can upload our changes!
//        questAutoSyncer.triggerAutoUpload();
//        Toast.makeText(this, "onOAuthAuthorizationVerified", Toast.LENGTH_LONG).show();


        //if SettingsFragment is open close any routing
        SettingsFragment SettingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SettingsFragment");
        if (SettingsFragment != null) {
            SettingsFragment.loadUsername();
        }

    }


    //login, logout from OSM
    public void loginToOSM() {
        OAuthWebViewDialogFragment dlg = OAuthWebViewDialogFragment.create(
                OAuth.createConsumer(), OAuth.createProvider());
        dlg.show(getFragmentManager(), OAuthWebViewDialogFragment.TAG);
    }

    public void logoutFromOSM() {
        OAuth.deleteConsumer(prefs);
        //clear the connection
        setupOsmConnection();

        //if SettingsFragment is open close any routing
        SettingsFragment SettingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SettingsFragment");
        if (SettingsFragment != null) {
            SettingsFragment.loadUsername();
        }
    }


/**********************************************End of Activity***********************************************/
}
