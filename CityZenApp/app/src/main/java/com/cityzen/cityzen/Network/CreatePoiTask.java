package com.cityzen.cityzen.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.cityzen.cityzen.OsmModule;
import com.cityzen.cityzen.Utils.StorageUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import de.westnordost.osmapi.OsmConnection;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * Created by Valdio Veliu on 11/05/2017.
 */

public class CreatePoiTask extends AsyncTask {
    private static final int DEFAULT_TIMEOUT = 45 * 1000;
    private SharedPreferences osmOauthData;

    private Context context;
    private String changesetId;
    private OsmOperationCallback callback;
    private OsmConnection osm;
    private Map<String, String> tagsToUpdate;
    private double latitude;
    private double longitude;
    private String response;

    public CreatePoiTask(Context context, OsmConnection osm, String changesetId, Map<String, String> tagsToUpdate, double latitude, double longitude, OsmOperationCallback callback) {
        this.context = context;
        this.changesetId = changesetId;
        this.callback = callback;
        this.osm = osm;
        osmOauthData = new StorageUtil(context).oauthStorage();
        this.tagsToUpdate = tagsToUpdate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            String body = XML_Util.createNodeXmlBody(tagsToUpdate, changesetId, latitude, longitude);
            response = createNode(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //response is the ID of the newly created element
        if (response == null || response.equals("Couldn't authenticate you")) {
            callback.onFailure(response);
        } else if (response != null && response.matches("\\d+(?:\\.\\d+)?")) {
            callback.osmOperationSuccessful(response);
        }
    }


    private String createNode(String requestBody) {
        HttpURLConnection connection = null;
        String result = null;
        try {
            URL url = new URL(OsmModule.OSM_API_URL + "node/create");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DEFAULT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_TIMEOUT);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "text/plain");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            //OAuth, authenticate call
            Authenticator.createOAuthConsumer(OsmModule.oAuthConsumer(osmOauthData)).sign(connection);

            //write body string to request
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(requestBody);
            writer.close();
            out.close();

            //establish connection
            connection.connect();

            //read response string
            StringBuffer sb = new StringBuffer();
            BufferedReader br;
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            } else {
                br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
            }
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
            //If no authentication applied --> Couldn't authenticate you
            //Created successfully --> 47976668 (changeset ID)

        } catch (IOException | OAuthExpectationFailedException | OAuthCommunicationException | OAuthMessageSignerException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
