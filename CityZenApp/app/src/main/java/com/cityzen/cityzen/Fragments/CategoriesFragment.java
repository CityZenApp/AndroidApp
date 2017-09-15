package com.cityzen.cityzen.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cityzen.cityzen.Adapters.CategoryAdapter;
import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Utils.Development.AppToast;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.OsmTag;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.FilterCategory;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.PoiResponseListener;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.QueryPois;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewItemClickInterface;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewTouchListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.metadude.java.library.overpass.models.Element;

public class CategoriesFragment extends Fragment {
    private TypedArray titles;
    private TypedArray categoryIDs;
    //integer to keep track of the number of OSM tag requests
    int poiTagsReceived = 0;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get POI data from resources
        Resources res = getActivity().getResources();
        titles = res.obtainTypedArray(R.array.poi_titles);
        categoryIDs = res.obtainTypedArray(R.array.poi_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
    }

    /**
     * Setup RecyclerView in {@link CategoriesFragment}
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), display);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // Item touch Listener
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewItemClickInterface() {
            @Override
            public void onClick(View view, int position) {
                try {
                    ((MainActivity) getActivity()).showLoadingScreen();
                    openCategory(position, ((MainActivity) getActivity()).getLastKnownLocation());
                    poiTagsReceived = 0;// reset the number
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    /**
     * Query all the points of interest from OSM, display in {@link PoiListFragment}
     *
     * @param position          Category ID
     * @param lastKnownLocation Devices last known location, around which to query points of interest
     * @throws IOException
     */
    private void openCategory(final int position, DeviceLocationData lastKnownLocation) throws IOException {
        boolean isGpsEnabled;
        boolean isNetworkEnabled;
        if (lastKnownLocation == null) {
            //check if GPS is enabled
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGpsEnabled || isNetworkEnabled) {
                new AppToast(getActivity()).centerViewToast(getString(R.string.calculating_device_location));

            } else
                new AppToast(getActivity()).centerViewToast(getString(R.string.enable_gps_to_continue));
            ((MainActivity) getActivity()).hideLoadingScreenWithNavigation();
            return;
        }
        final List<Element> poiElements = new ArrayList<>();
        QueryPois queryPois = new QueryPois(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), new PoiResponseListener() {
            @Override
            public void onPoiReceived(List<Element> elements) {
                for (Element element : elements)
                    poiElements.add(element);
                poiTagsReceived++;
                //if there are POI available, display them
                if (poiTagsReceived == FilterCategory.getFilters(position).size() && poiElements.size() > 0)
                    displayPOIs(titles.getString(position), poiElements, categoryIDs.getInteger(position, -1));
                else if (poiTagsReceived == FilterCategory.getFilters(position).size() && poiElements.size() == 0) {
                    try {
                        new AppToast(getActivity()).longToast(getString(R.string.no_pois_near_user_location));
                    } catch (Exception e) {
                    }
                    ((MainActivity) getActivity()).hideLoadingScreenWithNavigation();
                }
            }

            @Override
            public void onFailure() {
                try {
                    poiTagsReceived++;
                    // in case there are separate network calls and some fail
                    //if there are POI available, display them
                    if (poiTagsReceived == FilterCategory.getFilters(position).size() && poiElements.size() > 0)
                        displayPOIs(titles.getString(position), poiElements, categoryIDs.getInteger(position, -1));
                    else if (poiTagsReceived == FilterCategory.getFilters(position).size() && poiElements.size() == 0) {
                        new AppToast(getActivity()).toast(getString(R.string.no_pois_near_user_location));
                        ((MainActivity) getActivity()).hideLoadingScreenWithNavigation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //get the filter tags that will be requested in the API
        ArrayList<OsmTag> tags = FilterCategory.getFilters(position);
        for (OsmTag tag : tags) {
            queryPois.loadPois(tag.getKey(), tag.getValue());
        }
    }

    /**
     * Display list of POIs
     *
     * @param poiTitle   Category Title
     * @param elements   List of {@link Element}s to be displayed
     * @param categoryId
     */
    private void displayPOIs(String poiTitle, List<Element> elements, int categoryId) {
        try {
            ((MainActivity) getActivity()).openCategoryList(poiTitle, categoryId, elements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

