package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import com.cityzen.cityzen.Utils.MapUtils.OsmTags;
import com.cityzen.cityzen.Utils.RecyclerView.CategoryDisplayConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filter categories for POI ids.
 *
 * @author Valdio Veliu
 * @author Andy Scherzinger
 */
public class FilterCategory {
    private static final Map<Integer, List<OsmTag>> filterCategories = new HashMap<>();

    static {
        List<OsmTag> tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "bakery"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "pastry"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "confectionery"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_INDUSTRIAL, "bakery"));
        filterCategories.put(CategoryDisplayConfig.BAKERIES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "bar"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "nightclub"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "pub"));
        filterCategories.put(CategoryDisplayConfig.BARS.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "bicycle_rental"));
        filterCategories.put(CategoryDisplayConfig.BIKE_RENTAL.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "cafe"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "coffee"));
        filterCategories.put(CategoryDisplayConfig.CAFES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "car_rental"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "car_sharing"));
        filterCategories.put(CategoryDisplayConfig.CAR_RENTAL.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "fast_food"));
        filterCategories.put(CategoryDisplayConfig.FAST_FOOD.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "atm"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "bank"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "bureau_de_change"));
        filterCategories.put(CategoryDisplayConfig.FINANCIAL_SERVICES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "fuel"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "fuel"));
        filterCategories.put(CategoryDisplayConfig.GAS_STATIONS.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_BUILDING, "hotel"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "hotel"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "apartment"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "hostel"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "motel"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "guest_house"));
        filterCategories.put(CategoryDisplayConfig.HOTELS.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "marketplace"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_BUILDING, "retail"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_BUILDING, "kiosks"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "convenience"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "kiosks"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "supermarket"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "general"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "variety_store"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "tabacco"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "butcher"));
        filterCategories.put(CategoryDisplayConfig.MARKETS.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "mobile_phone"));
        filterCategories.put(CategoryDisplayConfig.MOBILE_STORES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "parking"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "parking_space"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_BUILDING, "parking"));
        filterCategories.put(CategoryDisplayConfig.PARKING.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "clinic"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "doctors"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "dentist"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "pharmacy"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "hospital"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "herbalist"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_HEALTHCARE, "alternative"));
        filterCategories.put(CategoryDisplayConfig.HEALTH_SERVICES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "post_office"));
        filterCategories.put(CategoryDisplayConfig.POST_OFFICES.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "restaurant"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "food_court"));
        filterCategories.put(CategoryDisplayConfig.RESTAURANTS.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "department_store"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "mall"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "clothes"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "shoes"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "jewelry"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "chemist"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "beauty"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "florist"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "photo"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_SHOP, "cosmetics"));
        filterCategories.put(CategoryDisplayConfig.SHOPPING.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "museum"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "information"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "attraction"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "yes"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_HISTORIC, "monument"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_TOURISM, "zoo"));
        filterCategories.put(CategoryDisplayConfig.TOURISM.id, tags);

        tags = new ArrayList<>();
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "taxi"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_AMENITY, "bus_station"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_RAILWAY, "station"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_HIGHWAY, "bus_stop"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_RAILWAY, "tram_stop"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_PUBLIC_TRANSPORT, "station"));
        tags.add(new OsmTag(OsmTags.OSM_KEY_RAILWAY, "subway_entrance"));
        filterCategories.put(CategoryDisplayConfig.TRANSPORTATION.id, tags);
    }

    /**
     * retrieve the {@link OsmTag}s for the POI id.
     * <p>
     * based on array <code>poi_types</code>.
     *
     * @param type the type of category
     * @return list of corresponding {@link OsmTag}s
     */
    public static List<OsmTag> getFilters(int type) {
        if (filterCategories.containsKey(type)) {
            return filterCategories.get(type);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * retrieve the {@link OsmTag}s for the POI id.
     * <p>
     * based on array <code>poi_types</code>.
     *
     * @return list of all categories and their {@link OsmTag}s
     */
    public static Map<Integer, List<OsmTag>> getAllFilters() {
        return filterCategories;
    }
}
