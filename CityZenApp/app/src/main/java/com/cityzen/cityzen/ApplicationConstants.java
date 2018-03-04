package com.cityzen.cityzen;

import android.content.Context;

import com.cityzen.cityzen.Models.OsmFeature;

import java.util.ArrayList;

public class ApplicationConstants {
    public final static String
            NAME = "CityZen Android app",
            VERSION = "v"+BuildConfig.VERSION_NAME,
            USER_AGENT = NAME + " " + VERSION;

    public static ArrayList<OsmFeature> OSM_TAGS = setupTags();

    private static ArrayList<OsmFeature> setupTags() {
        OSM_TAGS = new ArrayList<>();
        OSM_TAGS.add(new OsmFeature("amenity", "restaurant"));
        OSM_TAGS.add(new OsmFeature("amenity", "fast_food"));
        OSM_TAGS.add(new OsmFeature("amenity", "pub"));
        OSM_TAGS.add(new OsmFeature("amenity", "nightclub"));
        OSM_TAGS.add(new OsmFeature("amenity", "bar"));
        OSM_TAGS.add(new OsmFeature("amenity", "cafe"));
        OSM_TAGS.add(new OsmFeature("amenity", "ice_cream"));
        OSM_TAGS.add(new OsmFeature("amenity", "kindergarten"));
        OSM_TAGS.add(new OsmFeature("amenity", "school"));
        OSM_TAGS.add(new OsmFeature("amenity", "college"));
        OSM_TAGS.add(new OsmFeature("amenity", "library"));
        OSM_TAGS.add(new OsmFeature("amenity", "university"));
        OSM_TAGS.add(new OsmFeature("amenity", "bicycle_parking"));
        OSM_TAGS.add(new OsmFeature("amenity", "bicycle_rental"));
        OSM_TAGS.add(new OsmFeature("amenity", "bus_station"));
        OSM_TAGS.add(new OsmFeature("amenity", "car_rental"));
        OSM_TAGS.add(new OsmFeature("amenity", "car_wash"));
        OSM_TAGS.add(new OsmFeature("amenity", "fuel"));
        OSM_TAGS.add(new OsmFeature("amenity", "parking"));
        OSM_TAGS.add(new OsmFeature("amenity", "taxi"));
        OSM_TAGS.add(new OsmFeature("amenity", "atm"));
        OSM_TAGS.add(new OsmFeature("amenity", "bank"));
        OSM_TAGS.add(new OsmFeature("amenity", "pharmacy"));
        OSM_TAGS.add(new OsmFeature("amenity", "hospital"));
        OSM_TAGS.add(new OsmFeature("amenity", "clinic"));
        OSM_TAGS.add(new OsmFeature("amenity", "dentist"));
        OSM_TAGS.add(new OsmFeature("amenity", "doctors"));
        OSM_TAGS.add(new OsmFeature("amenity", "nursing_home"));
        OSM_TAGS.add(new OsmFeature("amenity", "veterinary"));
        OSM_TAGS.add(new OsmFeature("amenity", "arts_centre"));
        OSM_TAGS.add(new OsmFeature("amenity", "cinema"));
        OSM_TAGS.add(new OsmFeature("amenity", "community_centre"));
        OSM_TAGS.add(new OsmFeature("amenity", "theatre"));
        OSM_TAGS.add(new OsmFeature("amenity", "fountain"));
        OSM_TAGS.add(new OsmFeature("amenity", "court_house"));
        OSM_TAGS.add(new OsmFeature("amenity", "embassy"));
        OSM_TAGS.add(new OsmFeature("amenity", "fire_station"));
        OSM_TAGS.add(new OsmFeature("amenity", "police"));
        OSM_TAGS.add(new OsmFeature("amenity", "post_box"));
        OSM_TAGS.add(new OsmFeature("amenity", "post_office"));
        OSM_TAGS.add(new OsmFeature("amenity", "market_place"));
        OSM_TAGS.add(new OsmFeature("amenity", "place_of_worship"));
        OSM_TAGS.add(new OsmFeature("amenity", "telephone"));
        OSM_TAGS.add(new OsmFeature("amenity", "toilets"));


        OSM_TAGS.add(new OsmFeature("emergency", "ambulance_station"));
        OSM_TAGS.add(new OsmFeature("highway", "bus_stop"));

        OSM_TAGS.add(new OsmFeature("historic", "archaeological"));
        OSM_TAGS.add(new OsmFeature("historic", "castle"));
        OSM_TAGS.add(new OsmFeature("historic", "memorial"));
        OSM_TAGS.add(new OsmFeature("historic", "monument"));

        OSM_TAGS.add(new OsmFeature("public_transport", "station"));
        OSM_TAGS.add(new OsmFeature("public_transport", "stop_position"));

        OSM_TAGS.add(new OsmFeature("shop", "alcohol"));
        OSM_TAGS.add(new OsmFeature("shop", "baby_goods"));
        OSM_TAGS.add(new OsmFeature("shop", "bakery"));
        OSM_TAGS.add(new OsmFeature("shop", "bathroom_furnishing"));
        OSM_TAGS.add(new OsmFeature("shop", "beauty"));
        OSM_TAGS.add(new OsmFeature("shop", "beverages"));
        OSM_TAGS.add(new OsmFeature("shop", "bicycle"));
        OSM_TAGS.add(new OsmFeature("shop", "books"));
        OSM_TAGS.add(new OsmFeature("shop", "boutique"));
        OSM_TAGS.add(new OsmFeature("shop", "butcher"));
        OSM_TAGS.add(new OsmFeature("shop", "car"));
        OSM_TAGS.add(new OsmFeature("shop", "car_repair"));
        OSM_TAGS.add(new OsmFeature("shop", "car_parts"));
        OSM_TAGS.add(new OsmFeature("shop", "clothes"));
        OSM_TAGS.add(new OsmFeature("shop", "chemist"));
        OSM_TAGS.add(new OsmFeature("shop", "drugstore"));
        OSM_TAGS.add(new OsmFeature("shop", "drugstore"));
        OSM_TAGS.add(new OsmFeature("shop", "computer"));
        OSM_TAGS.add(new OsmFeature("shop", "electronics"));
        OSM_TAGS.add(new OsmFeature("shop", "hardware"));
        OSM_TAGS.add(new OsmFeature("shop", "department_store"));
        OSM_TAGS.add(new OsmFeature("shop", "dry_cleaning"));
        OSM_TAGS.add(new OsmFeature("shop", "trade"));
        OSM_TAGS.add(new OsmFeature("shop", "farm"));
        OSM_TAGS.add(new OsmFeature("shop", "florist"));
        OSM_TAGS.add(new OsmFeature("shop", "funeral_directors"));
        OSM_TAGS.add(new OsmFeature("shop", "furniture"));
        OSM_TAGS.add(new OsmFeature("shop", "garden_centre"));
        OSM_TAGS.add(new OsmFeature("shop", "gas"));
        OSM_TAGS.add(new OsmFeature("shop", "general"));
        OSM_TAGS.add(new OsmFeature("shop", "gift"));
        OSM_TAGS.add(new OsmFeature("shop", "glaziery"));
        OSM_TAGS.add(new OsmFeature("shop", "greengrocer"));
        OSM_TAGS.add(new OsmFeature("shop", "hairdresser"));
        OSM_TAGS.add(new OsmFeature("shop", "hearing_aids"));
        OSM_TAGS.add(new OsmFeature("shop", "herbalist"));
        OSM_TAGS.add(new OsmFeature("shop", "hifi"));
        OSM_TAGS.add(new OsmFeature("shop", "hunting"));
        OSM_TAGS.add(new OsmFeature("shop", "interior_decoration"));
        OSM_TAGS.add(new OsmFeature("shop", "jewelery"));
        OSM_TAGS.add(new OsmFeature("shop", "kiosk"));
        OSM_TAGS.add(new OsmFeature("shop", "kitchen"));
        OSM_TAGS.add(new OsmFeature("shop", "laundry"));
        OSM_TAGS.add(new OsmFeature("shop", "mall"));
        OSM_TAGS.add(new OsmFeature("shop", "massage"));
        OSM_TAGS.add(new OsmFeature("shop", "mobile_phone"));
        OSM_TAGS.add(new OsmFeature("shop", "money_lender"));
        OSM_TAGS.add(new OsmFeature("shop", "motorcycle"));
        OSM_TAGS.add(new OsmFeature("shop", "musical_instrument"));
        OSM_TAGS.add(new OsmFeature("shop", "newsagent"));
        OSM_TAGS.add(new OsmFeature("shop", "optician"));
        OSM_TAGS.add(new OsmFeature("shop", "organic"));
        OSM_TAGS.add(new OsmFeature("shop", "outdoor"));
        OSM_TAGS.add(new OsmFeature("shop", "paints"));
        OSM_TAGS.add(new OsmFeature("shop", "pet"));
        OSM_TAGS.add(new OsmFeature("shop", "radiotechnics"));
        OSM_TAGS.add(new OsmFeature("shop", "seafood"));
        OSM_TAGS.add(new OsmFeature("shop", "fish"));
        OSM_TAGS.add(new OsmFeature("shop", "second_hand"));
        OSM_TAGS.add(new OsmFeature("shop", "shoes"));
        OSM_TAGS.add(new OsmFeature("shop", "sports"));
        OSM_TAGS.add(new OsmFeature("shop", "stationery"));
        OSM_TAGS.add(new OsmFeature("shop", "supermarket"));
        OSM_TAGS.add(new OsmFeature("shop", "tattoo"));
        OSM_TAGS.add(new OsmFeature("shop", "ticket"));
        OSM_TAGS.add(new OsmFeature("shop", "tobacco"));
        OSM_TAGS.add(new OsmFeature("shop", "toys"));
        OSM_TAGS.add(new OsmFeature("shop", "vacuum_cleaner"));
        OSM_TAGS.add(new OsmFeature("shop", "video"));
        OSM_TAGS.add(new OsmFeature("shop", "window_blind"));

        OSM_TAGS.add(new OsmFeature("tourism", "alpine_hut"));
        OSM_TAGS.add(new OsmFeature("tourism", "attraction"));
        OSM_TAGS.add(new OsmFeature("tourism", "artworks"));
        OSM_TAGS.add(new OsmFeature("tourism", "camp_site"));
        OSM_TAGS.add(new OsmFeature("tourism", "caravan_site"));
        OSM_TAGS.add(new OsmFeature("tourism", "water_point"));
        OSM_TAGS.add(new OsmFeature("tourism", "chalet"));
        OSM_TAGS.add(new OsmFeature("tourism", "guest_house"));
        OSM_TAGS.add(new OsmFeature("tourism", "hostel"));
        OSM_TAGS.add(new OsmFeature("tourism", "hotel"));
        OSM_TAGS.add(new OsmFeature("tourism", "motel"));
        OSM_TAGS.add(new OsmFeature("tourism", "information"));
        OSM_TAGS.add(new OsmFeature("tourism", "museum"));
        OSM_TAGS.add(new OsmFeature("amenity", "museum"));
        OSM_TAGS.add(new OsmFeature("tourism", "picnic_site"));
        OSM_TAGS.add(new OsmFeature("tourism", "viewpoint"));
        OSM_TAGS.add(new OsmFeature("tourism", "theme_park"));
        OSM_TAGS.add(new OsmFeature("tourism", "zoo"));

        return OSM_TAGS;
    }
}
