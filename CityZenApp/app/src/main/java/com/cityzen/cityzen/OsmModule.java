package com.cityzen.cityzen;

import android.content.SharedPreferences;

import com.cityzen.cityzen.oauth.OAuth;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.changesets.ChangesetsDao;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.MapDataFactory;
import de.westnordost.osmapi.map.OsmMapDataFactory;
import de.westnordost.osmapi.notes.NotesDao;
import de.westnordost.osmapi.user.UserDao;
import oauth.signpost.OAuthConsumer;

public class OsmModule {
    public static String OSM_API_URL = "https://api.openstreetmap.org/api/0.6/";

    public static String OVERPASS_API_URL = "http://overpass-api.de/api/";

    public static OsmConnection osmConnection(OAuthConsumer consumer) {
        return new OsmConnection(OSM_API_URL, ApplicationConstants.USER_AGENT, consumer);
    }


    public static OAuthConsumer oAuthConsumer(SharedPreferences prefs) {
        return OAuth.loadConsumer(prefs);
    }

    public static MapDataFactory mapDataFactory() {
        return new OsmMapDataFactory();
    }

//    public static OverpassMapDataDao overpassMapDataDao(
//            Provider<OverpassMapDataParser> parserProvider) {
//        OsmConnection overpassConnection = new OsmConnection(
//                OVERPASS_API_URL, ApplicationConstants.USER_AGENT, null);
//        return new OverpassMapDataDao(overpassConnection, parserProvider);
//    }
//
//    public static OverpassMapDataParser overpassMapDataParser() {
//        return new OverpassMapDataParser(new ElementGeometryCreator(), new OsmMapDataFactory());
//    }

    public static ChangesetsDao changesetsDao(OsmConnection osm) {
        return new ChangesetsDao(osm);
    }

    public static UserDao userDao(OsmConnection osm) {
        return new UserDao(osm);
    }

    public static NotesDao notesDao(OsmConnection osm) {
        return new NotesDao(osm);
    }

    public static MapDataDao mapDataDao(OsmConnection osm) {
        return new MapDataDao(osm);
    }

//    public static QuestTypes questTypeList() {
//        return new QuestTypes(QuestTypes.TYPES);
//    }
}
