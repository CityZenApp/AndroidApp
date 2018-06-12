package com.cityzen.cityzen;

import android.content.SharedPreferences;

import com.cityzen.cityzen.oauth.OAuth;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.user.UserDao;
import oauth.signpost.OAuthConsumer;

public class OsmModule {
    public static String OSM_API_URL = "https://api.openstreetmap.org/api/0.6/";

    public static OsmConnection osmConnection(OAuthConsumer consumer) {
        return new OsmConnection(OSM_API_URL, ApplicationConstants.USER_AGENT, consumer);
    }

    public static OAuthConsumer oAuthConsumer(SharedPreferences prefs) {
        return OAuth.loadConsumer(prefs);
    }

    public static UserDao userDao(OsmConnection osm) {
        return new UserDao(osm);
    }
}
