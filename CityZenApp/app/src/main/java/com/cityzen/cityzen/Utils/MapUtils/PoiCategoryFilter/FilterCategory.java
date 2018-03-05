package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.util.ArrayList;

/**
 * Filter categories for POI ids.
 *
 * @author Valdio Veliu
 * @author Andy Scherzinger
 */
public class FilterCategory {

    private static final String OSM_KEY_SHOP = "shop";
    private static final String OSM_KEY_AMENITY = "amenity";
    private static final String OSM_KEY_TOURISM = "tourism";
    private static final String OSM_KEY_BUILDING = "building";
    private static final String OSM_KEY_INDUSTRIAL = "industrial";
    private static final String OSM_KEY_HEALTHCARE = "healthcare";
    private static final String OSM_KEY_HISTORIC = "historic";
    private static final String OSM_KEY_RAILWAY = "railway";
    private static final String OSM_KEY_HIGHWAY = "highway";
    private static final String OSM_KEY_PUBLIC_TRANSPORT = "public_transport";

    /**
     * retrieve the {@link OsmTag}s for the POI id.
     *
     * based on array <code>poi_types</code>.
     *
     * @param type the type of category
     * @return list of corresponding {@link OsmTag}s
     */
    public static ArrayList<OsmTag> getFilters(String type) {
        ArrayList<OsmTag> tags = new ArrayList<>();
        switch (type) {
            case "bakeries":
                tags.add(new OsmTag(OSM_KEY_SHOP, "bakery"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "pastry"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "confectionery"));
                tags.add(new OsmTag(OSM_KEY_INDUSTRIAL, "bakery"));
                break;
            case "bars":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "bar"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "nightclub"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "pub"));
                break;
            case "bike_rental":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "bicycle_rental"));
                break;
            case "cafes":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "cafe"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "coffee"));
                break;
            case "car_rental":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "car_rental"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "car_sharing"));
                break;
            case "fast_food":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "fast_food"));
                break;
            case "financial_services":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "atm"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "bank"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "bureau_de_change"));
                break;
            case "gas_stations":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "fuel"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "fuel"));
                break;
            case "hotels":
                tags.add(new OsmTag(OSM_KEY_BUILDING, "hotel"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "hotel"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "apartment"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "hostel"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "motel"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "guest_house"));
                break;
            case "markets":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "marketplace"));
                tags.add(new OsmTag(OSM_KEY_BUILDING, "retail"));
                tags.add(new OsmTag(OSM_KEY_BUILDING, "kiosks"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "convenience"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "kiosks"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "supermarket"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "general"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "variety_store"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "tabacco"));
                break;
            case "mobile_stores":
                tags.add(new OsmTag(OSM_KEY_SHOP, "mobile_phone"));
                break;
            case "parking":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "parking"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "parking_space"));
                tags.add(new OsmTag(OSM_KEY_BUILDING, "parking"));
                break;
            case "pharmacies":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "clinic"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "doctors"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "dentist"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "pharmacy"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "hospital"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "herbalist"));
                tags.add(new OsmTag(OSM_KEY_HEALTHCARE, "alternative"));
                break;
            case "post_offices":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "post_office"));
                break;
            case "restaurants":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "restaurant"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "food_court"));
                break;
            case "shopping":
                tags.add(new OsmTag(OSM_KEY_SHOP, "department_store"));
                tags.add(new OsmTag(OSM_KEY_SHOP, "mall"));
                break;
            case "tourism":
                tags.add(new OsmTag(OSM_KEY_TOURISM, "museum"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "information"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "attraction"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "yes"));
                tags.add(new OsmTag(OSM_KEY_HISTORIC, "monument"));
                tags.add(new OsmTag(OSM_KEY_TOURISM, "zoo"));
                break;
            case "transportation":
                tags.add(new OsmTag(OSM_KEY_AMENITY, "taxi"));
                tags.add(new OsmTag(OSM_KEY_AMENITY, "bus_station"));
                tags.add(new OsmTag(OSM_KEY_RAILWAY, "station"));
                tags.add(new OsmTag(OSM_KEY_HIGHWAY, "bus_stop"));
                tags.add(new OsmTag(OSM_KEY_RAILWAY, "tram_stop"));
                tags.add(new OsmTag(OSM_KEY_PUBLIC_TRANSPORT, "station"));
                tags.add(new OsmTag(OSM_KEY_RAILWAY, "subway_entrance"));
                break;
            default:
                break;
        }
        return tags;
    }
}
