package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.models.OverpassResponse;
import info.metadude.java.library.overpass.utils.NodesQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class QueryPois {

    private int radius = 1000; //600 m default
    private double latitude;
    private double longitude;
    private boolean sortByDistance = true;
    private int maxResponseCount = 10000;
    private PoiResponseListener listener;

    public QueryPois(int radius, double latitude, double longitude, boolean sortByDistance, int maxResponseCount, PoiResponseListener listener) {
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sortByDistance = sortByDistance;
        this.maxResponseCount = maxResponseCount;
        this.listener = listener;
    }

    public QueryPois(double latitude, double longitude, PoiResponseListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.listener = listener;
    }

    public QueryPois(int radius, double latitude, double longitude, PoiResponseListener listener) {
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listener = listener;
    }

    public void loadPois(final String key, final String value) throws IOException {
        Map<String, String> tags = new HashMap<String, String>() {
            {
                put(key, value);
            }
        };
        NodesQuery nodesQuery = new NodesQuery(radius, latitude, longitude, tags, sortByDistance, maxResponseCount);
        Call<OverpassResponse> streamsResponseCall = ApiModule.provideOverpassService()
                .getOverpassResponse(nodesQuery.getFormattedDataQuery());
        // Execute streamsResponse call to send a request to the Overpass-Turbo API.
        streamsResponseCall.enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response != null && response.body() != null && response.body().elements != null)
                    listener.onPoiReceived(response.body().elements);
                else
                    listener.onFailure();
            }

            @Override
            public void onFailure(Call<OverpassResponse> call, Throwable t) {
                listener.onFailure();
            }
        });
    }
}
