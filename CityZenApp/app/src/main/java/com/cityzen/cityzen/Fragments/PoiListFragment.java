package com.cityzen.cityzen.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Adapters.ElementListAdapter;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.MapUtils;
import com.cityzen.cityzen.Utils.MapUtils.OpeningHours.OpeningHoursUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmTags;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewItemClickInterface;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.metadude.java.library.overpass.models.Element;

public class PoiListFragment extends Fragment {

    private String poiName = "";
    private int categoryId = -1;
    private int color = 0;
    private List<Element> poiElements;
    private List<Element> adapterElements;
    private MapView map;
    private FloatingActionButton floatingActionButton;
    private boolean mapPreviewVisible = false;
    private boolean isFilterEnabled = false;
    private RelativeLayout relativeLayout;
    private LinearLayout filterLayoutContainer;
    private Toolbar toolbar;
    private CheckBox filterCheckBox;
    private CheckBox filterCheckBoxSortByName;
    private ElementListAdapter adapter;

    public PoiListFragment() {
        // Required empty public constructor
    }

    public static PoiListFragment newInstance(String poiName, int categoryId, int color) {
        PoiListFragment fragment = new PoiListFragment();
        Bundle args = new Bundle();
        args.putString("poiName", poiName);
        args.putInt("categoryId", categoryId);
        args.putInt("color", color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poiName = getArguments().getString("poiName");
            categoryId = getArguments().getInt("categoryId");
            color = getArguments().getInt("color");
        }

        //get form MainActivity
        poiElements = ((MainActivity) getActivity()).getPoiElementsOfCategory();
        //sort by closest to location
        if (poiElements != null) {
            List<Element> sortedElements = sortByClosestToLocation(
                    new ArrayList<>(poiElements),
                    ((MainActivity) getActivity()).getLastKnownLocation());
            poiElements = new ArrayList<>(sortedElements);
            adapterElements = new ArrayList<>(poiElements);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).hideLoadingScreen();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poi_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
        setupToolbarAndFilter();
        setupView();
        setupMapMarkers(adapterElements);

        //set color to the toolbar
        if (categoryId >= 0) {
            int categoryColor = getResources().getColor(color);
            toolbar.setBackgroundColor(categoryColor);
            filterLayoutContainer.setBackgroundColor(categoryColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(categoryColor);
            }
        }
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
        if (map != null)
            map.onDetach();

        //reset the status bar color when this Fragment is closed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    /**
     * Toolbar and POI filtering layout setup
     */
    private void setupToolbarAndFilter() {
        toolbar = getActivity().findViewById(R.id.poiListToolbar);
        filterCheckBox = getActivity().findViewById(R.id.filterCheckBox);
        filterCheckBoxSortByName = getActivity().findViewById(R.id.filterCheckBoxName);
        filterCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterByOpeningHours();
            }
        });
        filterCheckBoxSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterCheckBoxSortByName.isChecked()) {
                    List<Element> sortedElements = sortByName(new ArrayList<>(adapterElements));
                    adapterElements.clear();
                    adapterElements = new ArrayList<>(sortedElements);
                    adapter.resetAdapter(adapterElements);
                    setupMapMarkers(adapterElements);
                } else {

                    filterByOpeningHours();
                }
            }
        });
        filterLayoutContainer = getActivity().findViewById(R.id.filterPoiListContainer);
        toolbar.setTitle(poiName);
        toolbar.inflateMenu(R.menu.filter);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toggle filter
                if (!isFilterEnabled) {
//                    toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_filter_selected);
                    isFilterEnabled = true;
                    filterLayoutContainer.setVisibility(View.VISIBLE);
                } else {
                    filterCheckBox.setChecked(false);
                    isFilterEnabled = false;
                    toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_filter_white);
                    filterLayoutContainer.setVisibility(View.GONE);
                }
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showNavigationNoFab();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .remove(PoiListFragment.this)
                        .commit();
            }
        });
    }


    private void filterByOpeningHours() {
        if (filterCheckBox.isChecked()) {
            //filter Elements by opening hours
            List<Element> filteredElements = new ArrayList<>();
            for (Element element : adapterElements) {
                if (!element.tags.containsKey(OsmTags.OPENING_HOURS)) {
                    filteredElements.add(element);
                } else {
                    for (Map.Entry<String, String> tag : element.tags.entrySet()) {
                        if (tag.getKey().equals(OsmTags.OPENING_HOURS)) {
                            if (OpeningHoursUtils.isOpenNow(tag.getValue()) ||
                                    tag.getValue() == null || tag.getValue().equals(""))
                                filteredElements.add(element);
                        }
                    }
                }
            }
            adapterElements.clear();
            adapterElements = new ArrayList<>(filteredElements);
            //check if sort by name is checked
            if (filterCheckBoxSortByName.isChecked())
                adapterElements = sortByName(adapterElements);
            else {
                /*
                 * rest by location in not set sort by name
                 * implement sort by location
                 *
                 */
                List<Element> sortedByLocation = sortByClosestToLocation(
                        new ArrayList<>(adapterElements),
                        ((MainActivity) getActivity()).getLastKnownLocation());
                adapterElements.clear();
                adapterElements = new ArrayList<>(sortedByLocation);
            }
            adapter.resetAdapter(adapterElements);
            setupMapMarkers(adapterElements);

        } else {
            //reset adapter to its original state
            adapterElements.clear();
            adapterElements = new ArrayList<>(poiElements);
            //check if sort by name is checked
            if (filterCheckBoxSortByName.isChecked())
                adapterElements = sortByName(adapterElements);

            adapter.resetAdapter(adapterElements);
            setupMapMarkers(adapterElements);
        }
    }

    /**
     * Sort a given set of elements by name
     *
     * @param elements List of elements to be sorted
     * @return Sorted list of {@link Element}
     */
    private List<Element> sortByName(List<Element> elements) {
        if (elements.size() > 0)
            //bobble sort
            for (int i = 0; i < elements.size() - 1; i++) {
                for (int j = i + 1; j < elements.size(); j++) {
                    String name1 = "";
                    String name2 = "";
                    for (Map.Entry<String, String> tag : elements.get(i).tags.entrySet())
                        if (tag.getKey().equals("name"))
                            name1 = tag.getValue();

                    for (Map.Entry<String, String> tag : elements.get(j).tags.entrySet())
                        if (tag.getKey().equals("name"))
                            name2 = tag.getValue();


                    // Compare left to right, not right to left.
                    if (name1.compareToIgnoreCase(name2) > 0) {
                        Element temp = elements.get(i);
                        elements.set(i, elements.get(j));
                        elements.set(j, temp);
                    }
                }
            }
        return elements;
    }

    /**
     * Sort the list of elements form closest to furthest based on the provided location
     *
     * @param elements     List of elements to be sorted
     * @param locationData Location from witch to sort from
     * @return Sorted list of {@link Element}
     */
    private List<Element> sortByClosestToLocation(List<Element> elements, DeviceLocationData locationData) {
        if (elements != null && elements.size() > 0 && locationData != null) {

            int n = elements.size();
            Element temp;

            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    //compare the distances of objects to see who is closest
                    if (distance(elements.get(j - 1).lat, locationData.getLatitude(), elements.get(j - 1).lon, locationData.getLongitude(), 0, 0) >
                            distance(elements.get(j).lat, locationData.getLatitude(), elements.get(j).lon, locationData.getLongitude(), 0, 0)) {
                        temp = elements.get(j - 1);
                        elements.set(j - 1, elements.get(j));
                        elements.set(j, temp);
                    }

                }
            }
        }
        return elements;
    }


    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @return Distance in Meters
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


    /**
     * {@link PoiListFragment}  RecyclerView setup
     */
    private void setupRecyclerView() {
        if (adapter == null) {
            RecyclerView recyclerView = getActivity().findViewById(R.id.poiListRecyclerview);
            adapter = new ElementListAdapter(getActivity(), poiName, categoryId, adapterElements);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(
                    getActivity(), recyclerView, new RecyclerViewItemClickInterface() {
                @Override
                public void onClick(View view, int position) {
                    openDetailedInfo(new ParcelablePOI(adapterElements.get(position)));
                }

                @Override
                public void onLongClick(View view, int position) {
                    //on long click open map .... do display a marker ...
                    floatingActionButton.setImageResource(R.drawable.ic_close_white);
                    relativeLayout.setVisibility(View.VISIBLE);
                    openMapPreview(adapterElements.get(position));
                    mapPreviewVisible = true;
                }
            }));
        } else {
            adapter.resetAdapter(adapterElements);
        }
    }

    /**
     * Setup {@link PoiListFragment} static views and their click listeners
     */
    private void setupView() {
        TextView osmCopyright = getActivity().findViewById(R.id.osmCopyright);
        osmCopyright.setText(Html.fromHtml(getString(R.string.osm_copyright)));

        relativeLayout = getActivity().findViewById(R.id.mapViewPoiListContainer);
        floatingActionButton = getActivity().findViewById(R.id.fabPoiList);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mapPreviewVisible) {
                    floatingActionButton.setImageResource(R.drawable.ic_close_white);
                    relativeLayout.setVisibility(View.VISIBLE);
                    mapPreviewVisible = true;
                } else {
                    floatingActionButton.setImageResource(R.drawable.ic_map_white);
                    relativeLayout.setVisibility(View.GONE);
                    mapPreviewVisible = false;
                }
            }
        });
    }

    /**
     * Open {@link PoiDetailsFragment} dialog window to show detailed info on POI
     *
     * @param poi POI preview element
     */
    private void openDetailedInfo(ParcelablePOI poi) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PoiDetailsFragment dialogFragment = PoiDetailsFragment.newInstance(poi);
        dialogFragment.show(fm, "PoiDetailsFragment");
    }

    /**
     * Show marker of POI on map preview, on {@link RecyclerView} long press
     *
     * @param element POI to  display
     */
    private void openMapPreview(Element element) {
        if (map == null) {
            //important! set your user agent to prevent getting banned from the osm servers
            Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

            map = getActivity().findViewById(R.id.mapPoiList);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setTilesScaledToDpi(true);

            // add multi-touch capability
            map.setMultiTouchControls(true);

            // add compass to map
            CompassOverlay compassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), map);
            compassOverlay.enableCompass();
            map.getOverlays().add(compassOverlay);
        }
        // get map controller
        IMapController controller = map.getController();
        GeoPoint position = new GeoPoint(element.lat, element.lon);
        controller.setCenter(position);
        controller.setZoom(18);

        MapUtils.addMarker(getActivity(), map, element);
    }

    /**
     * Show all markers of the POIs on the map preview
     *
     * @param elements List of POIs to display
     */
    private void setupMapMarkers(List<Element> elements) {
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

        //clear existing markers
        if (map != null)
            map.getOverlays().clear();
        map = getActivity().findViewById(R.id.mapPoiList);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);

        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // get map controller
        IMapController controller = map.getController();

        for (Element element : elements)
            MapUtils.addMarker(getActivity(), map, element);

        if (elements.size() > 0) {
            GeoPoint position = new GeoPoint(elements.get(0).lat, elements.get(0).lon);
            controller.setCenter(position);
            controller.setZoom(14);
        }
        setupMyLocation();
    }

    /**
     * Show location marker for user
     */
    private void setupMyLocation() {
        DeviceLocationData location = ((MainActivity) getActivity()).getLastKnownLocation();
        if (location != null) {
            MapUtils.myLocation(getActivity(), map, location.getLatitude(), location.getLongitude());
            //center to my location
            GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().setCenter(position);
        }
    }
}
