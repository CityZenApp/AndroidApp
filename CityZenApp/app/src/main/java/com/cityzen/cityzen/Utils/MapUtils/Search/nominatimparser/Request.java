package com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cityzen.cityzen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    private static final String TAG = Request.class.getName();

    public static void getPlaces(Context context, Action a, ArrayList<Pair>... parameters) {
        new GetPlaces(context, a, parameters).execute();
    }

    private static class GetPlaces extends AsyncTask<Pair, Place, Void> {
        /*
            wiki : http://wiki.openstreetmap.org/wiki/Nominatim

            street=<housenumber> <streetname>
            city=<city>
            county=<county>
            state=<state>
            country=<country>
            postalcode=<postalcode>

            use q= if you don't know whether the user type an address, a city a county or whatever
        */
        private ArrayList<Place> queriedPlaces = new ArrayList<>();
        private String error = null;
        private final String QUERY = "https://nominatim.openstreetmap.org/search?";
        private Action action;
        private WeakReference<Context> context;
        private ArrayList<Pair>[] parameters;

        /**
         * @param action     The method to apply on each Place which is returned by nominatim
         * @param parameters A set of keys and values to provide to the request. Each map will be triggered in a different request
         * @see Action
         */
        public GetPlaces(Context context, Action action, ArrayList<Pair>... parameters) {
            this.context = new WeakReference<>(context);
            this.action = action;
            this.parameters = parameters;
        }

        @Override
        protected Void doInBackground(Pair... params) {
            StringBuilder jsonResult = new StringBuilder();
            StringBuilder sb = new StringBuilder(QUERY);
            sb.append("format=json&polygon=0&addressdetails=1&extratags=1&limit=50&");
            for (ArrayList<Pair> pairs : parameters) {
                for (Pair p : pairs) {
                    sb.append(p.first).append("=").append(p.second.replace(" ", "+")).append("&");
                }
                try {
                    URL url = new URL(sb.toString().substring(0, sb.toString().length() - 1));//remove last '&', crashes the request in some devices
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int status = conn.getResponseCode();
                    InputStreamReader in;
                    //make sure the connection is successful
                    if (status != HttpURLConnection.HTTP_OK) {
                        in = new InputStreamReader(conn.getErrorStream());
                    } else {
                        in = new InputStreamReader(conn.getInputStream());
                    }

                    BufferedReader jsonReader = new BufferedReader(in);
                    String lineIn;
                    while ((lineIn = jsonReader.readLine()) != null) {
                        jsonResult.append(lineIn);
                    }

                    if (status == HttpURLConnection.HTTP_OK) {
                        try {
                            JSONArray jsonArray = new JSONArray(jsonResult.toString());
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                long place_id = jsonObject.optLong("place_id");
                                String license = jsonObject.optString("license");
                                String osm_type = jsonObject.optString("osm_type");
                                long osm_id = jsonObject.optLong("osm_id");
                                JSONArray boundingArray = jsonObject.getJSONArray("boundingbox");
                                BoundingBox boundingBox = new BoundingBox();
                                for (int j = 0; j < boundingArray.length(); j++) {
                                    boundingBox.setBound(j, boundingArray.optDouble(j));
                                }
                                double lat = jsonObject.optDouble("lat");
                                double lon = jsonObject.optDouble("lon");
                                String display_name = jsonObject.optString("display_name");
                                String entityClass = jsonObject.optString("class");
                                String type = jsonObject.optString("type");
                                float importance = (float) jsonObject.optDouble("importance");
                                //extract tags
                                Map<String, String> tags = new HashMap<>();
                                if (jsonObject.has("address")) {
                                    parseAndAddAdressTags(tags, jsonObject.getString("address"));
                                }
                                parseExtraTags(tags, jsonObject.getString("extratags"));

                                queriedPlaces.add(new Place(
                                        place_id,
                                        osm_id,
                                        lat,
                                        lon,
                                        importance,
                                        license,
                                        osm_type,
                                        display_name,
                                        entityClass,
                                        type,
                                        boundingBox,
                                        tags)
                                );
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Error reading server response", e);
                            if (context.get() != null) {
                                error = context.get().getString(R.string.server_response_error);
                            } else {
                                error = "Error reading server response";
                            }
                        }
                    } else {
                        if (context.get() != null) {
                            error = context.get().getString(R.string.server_error) + ": " + jsonResult.toString();
                        } else {
                            error = "Error communicating with server: " + jsonResult.toString();
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error communicating with server", e);
                    if (context.get() != null) {
                        error = context.get().getString(R.string.server_error);
                    } else {
                        error = "Error communicating with server";
                    }
                }
            }
            return null;
        }

        private void parseAndAddAdressTags(Map<String, String> tags, String address) throws JSONException {
            JSONObject jObject = new JSONObject(address);
            Iterator iter = jObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = jObject.getString(key);
                if ("road".equals(key)) {
                    tags.put("addr:street", value);
                } else if ("house_number".equals(key)) {
                    tags.put("addr:housenumber", value);
                } else {
                    tags.put("addr:" + key, value);
                }
            }
        }

        private void parseExtraTags(Map<String, String> tags, String s) throws JSONException {
            JSONObject jObject = new JSONObject(s);

            Iterator iter = jObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = jObject.getString(key);
                tags.put(key, value);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            action.action(queriedPlaces, error);
            context = null;
        }
    }
}

