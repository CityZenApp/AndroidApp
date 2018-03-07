package com.cityzen.cityzen.Utils;

import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;

import java.util.Map;

/**
 * Created by scherzia on 07.03.2018.
 */
public abstract class PoiHelper {
    public static String createAddressDisplayString(ParcelablePOI poi) {
        return createAddressDisplayString(poi.getTags());
    }

    public static String createAddressDisplayString(Place poi) {
        return createAddressDisplayString(poi.getTags());
    }

    private static String createAddressDisplayString(Map<String, String> tags) {
        String address = "";
        if (tags.containsKey("addr:street")) {
            if (tags.containsKey("addr:housenumber")) {
                address += tags.get("addr:street") + " " + tags.get("addr:housenumber");
            } else {
                address = tags.get("addr:street");
            }
        }

        if ((tags.containsKey("addr:postcode") || tags.containsKey("addr:city")) && address.length() > 0) {
            address += "\n";
        }

        if (tags.containsKey("addr:postcode") && tags.containsKey("addr:city")) {
            address += tags.get("addr:postcode") + " " + tags.get("addr:city");
        } else if (tags.containsKey("addr:postcode")) {
            address += tags.get("addr:postcode");
        } else if (tags.containsKey("addr:city")) {
            address += tags.get("addr:city");
        }

        return address;
    }
}
