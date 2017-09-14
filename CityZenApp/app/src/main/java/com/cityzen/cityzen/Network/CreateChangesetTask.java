package com.cityzen.cityzen.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.cityzen.cityzen.OsmModule;
import com.cityzen.cityzen.Utils.Development.AppLog;
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

public class CreateChangesetTask extends AsyncTask {
    private static final int DEFAULT_TIMEOUT = 45 * 1000;
    private SharedPreferences osmOauthData;

    private Context context;
    private ChangesetCallback callback;
    private OsmConnection osm;
    private Map<String, String> tags;
    private String response = null;

    public CreateChangesetTask(Context context, OsmConnection osm, Map<String, String> tags, ChangesetCallback callback) {
        this.context = context;
        this.callback = callback;
        this.osm = osm;
        this.tags = tags;
        osmOauthData = new StorageUtil(context).oauthStorage();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        //changeset call response String
        String body;
        try {
            body = XML_Util.createChangesetXmlBody(tags);
        } catch (Exception e) {
            e.printStackTrace();
            body = null;
        }
        if (body != null) {
            response = createChangeset(OsmModule.OSM_API_URL + "changeset/create", "PUT", body);
//            AppLog.log(response);
//            AppLog.log("Subscribed to: " + subscribeUserToChangeset(response));
//            AppLog.log("--------------------------------------");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (response != null && response.matches("\\d+(?:\\.\\d+)?")) {
            //is number
            callback.onChangesetCreated(response);
        } else {
            callback.onFailure(response);
        }
    }

    private String createChangeset(String endpointUrl, String method, String requestBody) {
        HttpURLConnection connection = null;
        String result = null;
        try {
            URL url = new URL(endpointUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DEFAULT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_TIMEOUT);
            connection.setRequestMethod(method);
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

            /** Request call data, header and connection data*/
//            Map<String, List<String>> vv = connection.getHeaderFields();
//            for (Map.Entry<String, List<String>> entry : vv.entrySet()) {
//                Log.wtf("CHANGESET", entry.getKey());
//                List l = entry.getValue();
//                for (int i = 0; i < l.size(); i++)
//                    Log.wtf("CHANGESET", String.valueOf(l.get(i)));
//                Log.wtf("CHANGESET", "--------------------------------------------");
//
//            }
//            Log.wtf("OSM_CONNxEXTION", String.valueOf(connection.getResponseCode()));
//            Log.wtf("OSM_CONNEXTION", connection.getRequestMethod());
//            Log.wtf("OSM_CONNEXTION", connection.getResponseMessage());


        } catch (IOException | OAuthExpectationFailedException | OAuthCommunicationException | OAuthMessageSignerException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String subscribeUserToChangeset(String changesetID) {
        ///changeset/#id/subscribe
        HttpURLConnection connection = null;
        String result = null;
        try {
            URL url = new URL(OsmModule.OSM_API_URL + "changeset/" + changesetID + "/subscribe");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DEFAULT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            //OAuth, authenticate call
            Authenticator.createOAuthConsumer(OsmModule.oAuthConsumer(osmOauthData)).sign(connection);

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
