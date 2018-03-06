package com.cityzen.cityzen.Utils.RecyclerView;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.cityzen.cityzen.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Display configuration for all known categories.
 */
public enum CategoryDisplayConfig {
    UNDEFINED("undefined", R.drawable.ic_map_marker, R.color.category_building),
    BAKERIES("bakeries", R.drawable.ic_local_bakery, R.color.category_bakery),
    BARS("bars", R.drawable.ic_local_bar_white_24dp, R.color.category_bar),
    BIKE_RENTAL("bike_rental", R.drawable.ic_local_bike, R.color.category_bike_rental),
    BUILDINGS("building", R.drawable.ic_local_city, R.color.category_building),
    CAFES("cafes", R.drawable.ic_local_cafe_white, R.color.category_cafe),
    CAR_RENTAL("car_rental", R.drawable.ic_local_car, R.color.category_car_rental),
    FAST_FOOD("fast_food", R.drawable.ic_local_fast_food, R.color.category_fast_food),
    FINANCIAL_SERVICES("financial_services", R.drawable.ic_local_atm_white, R.color.category_atm),
    GAS_STATIONS("gas_stations", R.drawable.ic_local_gas_station_white, R.color.category_gas_station),
    HOTELS("hotels", R.drawable.ic_home_white, R.color.category_accomodation),
    MARKETS("markets", R.drawable.ic_store_white_24dp, R.color.category_market),
    MOBILE_STORES("mobile_stores", R.drawable.ic_phonelink_setup_white, R.color.category_mobile_phone_store),
    PARKING("parking", R.drawable.ic_local_parking, R.color.category_parking),
    PHARMACIES("pharmacies", R.drawable.ic_local_doctor_white, R.color.category_pharmacy),
    POST_OFFICES("post_offices", R.drawable.ic_local_post_office, R.color.category_post_office),
    RESTAURANTS("restaurants", R.drawable.ic_restaurant_white, R.color.category_restaurant),
    SHOPPING("shopping", R.drawable.ic_local_mall_white_24dp, R.color.category_shopping_mall),
    TOURISM("tourism", R.drawable.ic_nature_people_white, R.color.category_tourism),
    TRANSPORTATION("transportation", R.drawable.ic_local_transit_station_white, R.color.category_transportation);

    private static final Map<String, CategoryDisplayConfig> reverseMap = new HashMap<>();

    static {
        CategoryDisplayConfig[] values = CategoryDisplayConfig.values();
        for (CategoryDisplayConfig config : values) {
            reverseMap.put(config.id, config);
        }
    }

    public String id;
    public @DrawableRes int icon;
    public @ColorRes int color;

    CategoryDisplayConfig(String id, @DrawableRes int icon, @ColorRes int color) {
        this.id = id;
        this.icon = icon;
        this.color = color;
    }

    public static CategoryDisplayConfig getById(String key) {
        if (reverseMap.containsKey(key)) {
            return reverseMap.get(key);
        } else {
            return UNDEFINED;
        }
    }
}
