package com.cityzen.cityzen.oauth;

import android.content.SharedPreferences;

import com.cityzen.cityzen.Prefs;

import java.util.Arrays;
import java.util.List;

import de.westnordost.osmapi.user.Permission;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class OAuth {
    public static final String TAG = "OAuth";

    private static final String CONSUMER_KEY = "LdMKcdJMW6EB3Vbnp9aHN4cD26MhY0G44Fqsfgca";
    private static final String CONSUMER_SECRET = "UqzrF44LeOLOSaCxLgE19z8fTpjrvnqa5bw5lNdI";

    private static final String BASE_OAUTH_URL = "https://www.openstreetmap.org/oauth/";

    public static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
            Permission.READ_PREFERENCES_AND_USER_DETAILS,
            Permission.MODIFY_MAP,
            Permission.WRITE_NOTES);

    public static OAuthProvider createProvider() {
        return new DefaultOAuthProvider(
                BASE_OAUTH_URL + "request_token",
                BASE_OAUTH_URL + "access_token",
                BASE_OAUTH_URL + "authorize");
    }

    public static OAuthConsumer createConsumer() {
        return new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    }

    public static OAuthConsumer loadConsumer(SharedPreferences prefs) {
        OAuthConsumer result = createConsumer();
        String accessToken = prefs.getString(Prefs.OAUTH_ACCESS_TOKEN, null);
        String accessTokenSecret = prefs.getString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, null);
        result.setTokenWithSecret(accessToken, accessTokenSecret);
        return result;
    }

    public static void saveConsumer(SharedPreferences prefs, OAuthConsumer consumer) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Prefs.OAUTH_ACCESS_TOKEN, consumer.getToken());
        editor.putString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, consumer.getTokenSecret());
        editor.apply();
    }

    public static void deleteConsumer(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Prefs.OAUTH_ACCESS_TOKEN);
        editor.remove(Prefs.OAUTH_ACCESS_TOKEN_SECRET);
        editor.apply();
    }

    public static boolean isAuthorized(SharedPreferences prefs) {
        return prefs.getString(Prefs.OAUTH_ACCESS_TOKEN_SECRET, null) != null;
    }
}
