package com.cityzen.cityzen.Fragments.PoiFeature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cityzen.cityzen.Adapters.PoiFeaturesAdapter;
import com.cityzen.cityzen.ApplicationConstants;
import com.cityzen.cityzen.Models.OsmFeature;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewItemClickInterface;
import com.cityzen.cityzen.Utils.RecyclerView.RecyclerViewTouchListener;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Valdio Veliu on 14/05/2017.
 */

public class PoiFeatureDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private PoiFeaturesAdapter adapter;
    private FeatureSelectedCallback callback;

    private ArrayList<OsmFeature> displayItems;
    private SearchView featuresSearchView;
    private RelativeLayout featuresSearchViewContainer;

    public PoiFeatureDialogFragment() {
    }

    public static PoiFeatureDialogFragment newInstance() {
        PoiFeatureDialogFragment fragment = new PoiFeatureDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.poi_feature_dialog_layout, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayItems = new ArrayList<>(ApplicationConstants.OSM_TAGS);
        setupRecyclerView();
        setupView();
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            recyclerView = (RecyclerView) getDialog().findViewById(R.id.featuresRecyclerView);
            adapter = new PoiFeaturesAdapter(getActivity(), displayItems);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewItemClickInterface() {
                @Override
                public void onClick(View view, int position) {
                    featuresSearchView.clearFocus();
                    // Do something with the time chosen by the user
                    callback = (FeatureSelectedCallback) getTargetFragment();
                    callback.onFeatureSelected(displayItems.get(position));
                    //close dialog fragment
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .remove(PoiFeatureDialogFragment.this)
                            .commit();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else {
            adapter.resetAdapter(displayItems);
        }
    }

    private void setupView() {
        featuresSearchViewContainer = (RelativeLayout) getDialog().findViewById(R.id.featuresSearchViewContainer);
        featuresSearchViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                featuresSearchView.setIconified(false);//open searchView
            }
        });
        featuresSearchView = (SearchView) getDialog().findViewById(R.id.featuresSearchView);
        featuresSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    displayItems.clear();
                    displayItems = new ArrayList<OsmFeature>(filterBySearch(query));
                    adapter.resetAdapter(displayItems);
                } else if (query.length() == 0) {
                    displayItems.clear();
                    displayItems = new ArrayList<OsmFeature>(ApplicationConstants.OSM_TAGS);
                    adapter.resetAdapter(displayItems);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 0) {
                    displayItems.clear();
                    displayItems = new ArrayList<OsmFeature>(filterBySearch(query));
                    adapter.resetAdapter(displayItems);
                } else if (query.length() == 0) {
                    displayItems.clear();
                    displayItems = new ArrayList<OsmFeature>(ApplicationConstants.OSM_TAGS);
                    adapter.resetAdapter(displayItems);
                }
                return false;
            }
        });
    }

    private ArrayList<OsmFeature> filterBySearch(String searchString) {
        ArrayList<OsmFeature> features = new ArrayList<>();

        for (OsmFeature feature : ApplicationConstants.OSM_TAGS)
            if (feature.getKey().contains(searchString.toLowerCase()) || feature.getValue().contains(searchString.toLowerCase()))
                features.add(feature);
        return features;
    }

}
