package com.cityzen.cityzen.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Adapters.PlaceListAdapter;
import com.cityzen.cityzen.ApplicationConstants;
import com.cityzen.cityzen.Models.DeviceLocationData;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.DeviceUtils.Connectivity;
import com.cityzen.cityzen.Utils.DeviceUtils.DeviceUtils;
import com.cityzen.cityzen.Utils.MapUtils.OpeningHours.OpeningHoursUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmTags;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Action;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Pair;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Request;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewItemClickInterface;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewTouchListener;
import com.cityzen.cityzen.Utils.RecyclerView.SimpleDividerItemDecoration;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private Toolbar toolbar;
    private CheckBox filterCheckBox;
    private LinearLayout filterLayoutContainer;
    private LinearLayout emptyView;
    private boolean isFilterEnabled = false;
    private SearchView searchView;
    //List of the places that are queried in OSM search
    private List<Place> searchedPlaces = new ArrayList<>();
    private List<Place> adapterElements = new ArrayList<>();
    private String searchText;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupView();
        setupToolbarAndFilter();
        clearSearchView();
    }

    private void setupView() {
        emptyView = getActivity().findViewById(R.id.emptySearchResult);
        searchView = getActivity().findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            boolean isTyping = false;

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                searchedPlaces.clear();//clear all previously searched places
                adapterElements.clear();
                if (query != null && query.length() > 0) {//at least one character
                    search(query);
                } else {
                    searchedPlaces.clear();//clear all previously searched places
                    adapterElements.clear();
                    //reset the recyclerView
                    adapter.resetAdapter();
                }
                return false;
            }


            private Timer timer = new Timer();
            private final long DELAY = 600; // milliseconds

            @Override
            public boolean onQueryTextChange(final String query) {
                searchText = query;
                if ((query == null || query.equals("")) && adapterElements != null && adapter != null) {
                    searchedPlaces.clear();//clear all previously searched places
                    adapterElements.clear();
                    //reset the recyclerView
                    adapter.resetAdapter();
                } else {
                    //automatic search
                    if (!isTyping) {
//                        AppLog.log("started typing");
                        // Send notification for start typing event
                        isTyping = true;
                    }
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    isTyping = false;
//                                    AppLog.log("stopped typing");
                                    //send notification for stopped typing event
                                    //searching on text change with DELAY time of search
                                    if (Connectivity.isConnected(getActivity()) && Connectivity.isConnectedFast(getActivity())) {
                                        searchedPlaces.clear();//clear all previously searched places
                                        adapterElements.clear();
                                        if (query != null && query.length() > 0)//at least one character
                                            search(query);
                                        else {
                                            searchedPlaces.clear();//clear all previously searched places
                                            adapterElements.clear();
                                            //reset the recyclerView
                                            if (adapter != null)
                                                adapter.resetAdapter();
                                        }
                                    }
                                }
                            },
                            DELAY
                    );

                }
                return false;
            }
        });
    }

    private void setupToolbarAndFilter() {
        toolbar = getActivity().findViewById(R.id.searchToolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);//open searchView
            }
        });
        filterCheckBox = getActivity().findViewById(R.id.filterCheckBox);
        filterCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AppLog.log(searchedPlaces.size());
                filterElements();
            }
        });
        filterLayoutContainer = getActivity().findViewById(R.id.filterPoiListContainer);
        toolbar.inflateMenu(R.menu.filter);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toggle filter
                if (!isFilterEnabled) {
                    toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_filter_selected);
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
    }

    private void filterElements() {
        if (adapter == null) return;
        if (filterCheckBox.isChecked()) {
            //filter Elements by opening hours
            List<Place> filteredElements = new ArrayList<>();
            for (Place place : adapterElements) {
                if (!place.getTags().containsKey(OsmTags.OPENING_HOURS)) {
                    filteredElements.add(place);
                } else {
                    for (Map.Entry<String, String> tag : place.getTags().entrySet()) {
                        if (tag.getKey().equals(OsmTags.OPENING_HOURS)) {
                            if (OpeningHoursUtils.isOpenNow(tag.getValue()) ||
                                    tag.getValue() == null || tag.getValue().equals(""))
                                filteredElements.add(place);
                        }
                    }
                }
            }
            adapterElements.clear();
            adapterElements = new ArrayList<>(filteredElements);
            adapter.resetAdapter(adapterElements);

        } else {
            //reset adapter to its original state
            adapterElements.clear();
            adapterElements = new ArrayList<>(searchedPlaces);
            adapter.resetAdapter(adapterElements);
        }

        if (adapter.getItemCount() < 1) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() throws Exception {
        if (adapter == null) {
            recyclerView = getActivity().findViewById(R.id.searchRecyclerView);
            adapter = new PlaceListAdapter(getActivity(), adapterElements);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

            // Item touch Listener
            recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewItemClickInterface() {
                @Override
                public void onClick(View view, int position) {
                    searchView.clearFocus();
                    openDetailedInfo(new ParcelablePOI(adapterElements.get(position)));
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else {
            adapter.resetAdapter(adapterElements);
        }
    }

    private void openDetailedInfo(ParcelablePOI poi) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PoiDetailsFragment dialogFragment = PoiDetailsFragment.newInstance(poi);
        dialogFragment.show(fm, "PoiDetailsFragment");
    }

    /**
     * Method not used, left for later updates
     * Maybe tag oriented searches
     */
    private void searchForPoiTypes(final double lat, final double lon, final double altitude, final String poiType) throws IOException {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //altitude set to 13 or smth.... ???
                NominatimPOIProvider poiProvider = new NominatimPOIProvider(ApplicationConstants.USER_AGENT);
                ArrayList<POI> pois = poiProvider.getPOICloseTo(new GeoPoint(lat, lon, altitude), poiType, 50, 0.1);
                //or : ArrayList<POI> pois = poiProvider.getPOIAlong(road.getRouteLow(), "fuel", 50, 2.0);
                return null;
            }
        }.execute();
    }

    /**
     * Search OSM for places with the specified name
     *
     * @param searchString Name of the place to be searched
     */
    private void search(String searchString) {
        DeviceLocationData deviceLocationData = ((MainActivity) getActivity()).getLastKnownLocation();
        Action action = new Action() {
            @Override
            public void action(ArrayList<Place> places) {
                searchedPlaces = places;
                adapterElements.clear();
                adapterElements = new ArrayList<>(searchedPlaces);
                try {
                    setupRecyclerView();
                } catch (Exception ignored) {
                }
                filterElements();//filter elements if needed
            }
        };
        ArrayList<Pair> pairs = new ArrayList<>();
        pairs.add(new Pair("q=", searchString));
        if (deviceLocationData != null && deviceLocationData.getLocality() != null)
            pairs.add(new Pair("q=", searchString + " " + deviceLocationData.getLocality()));
//        pairs.add(new Pair("q=", searchString + " " + deviceLocationData.getCountryName()));
        if (DeviceUtils.isInternetConnected(getActivity()))
            Request.getPlaces(action, pairs);
    }


    private void clearSearchView() {
        Timer timer = new Timer();
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (searchView != null && adapterElements != null && adapter != null)
                        if ((searchText == null || searchText.equals(""))) {
                            if (getActivity() != null)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchedPlaces.clear();//clear all previously searched places
                                        adapterElements.clear();
                                        //reset the recyclerView
                                        adapter.resetAdapter();
                                    }
                                });
                        }
                }
            }, 0, 1000);//Clear view every second
        } catch (Exception e) {
            timer.cancel();
        }
    }
}
