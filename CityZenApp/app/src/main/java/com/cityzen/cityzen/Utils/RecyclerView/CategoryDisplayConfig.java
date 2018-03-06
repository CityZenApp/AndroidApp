package com.cityzen.cityzen.Utils.RecyclerView;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.cityzen.cityzen.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Display configuration for all known categories.
 */
public enum CategoryDisplayConfig {
    UNDEFINED(-1, R.string.Location, R.drawable.ic_map_marker, R.color.category_building),
    BAKERIES(0, R.string.Bakeries, R.drawable.ic_local_bakery, R.color.category_bakery),
    BARS(1, R.string.Bars_Clubs, R.drawable.ic_local_bar_white_24dp, R.color.category_bar),
    BIKE_RENTAL(2, R.string.RentBicycle, R.drawable.ic_local_bike, R.color.category_bike_rental),
    BUILDINGS(-2, R.string.Buildings, R.drawable.ic_local_city, R.color.category_building),
    SCHOOL(-3, R.string.Education, R.drawable.ic_local_school, R.color.category_building),
    CAFES(3, R.string.Cafes, R.drawable.ic_local_cafe_white, R.color.category_cafe),
    CAR_RENTAL(4, R.string.RentCar_CareSharing, R.drawable.ic_local_car, R.color.category_car_rental),
    FAST_FOOD(5, R.string.FastFood, R.drawable.ic_local_fast_food, R.color.category_fast_food),
    FINANCIAL_SERVICES(6, R.string.Banks_ATMs_Exchange, R.drawable.ic_local_atm_white, R.color.category_atm),
    GAS_STATIONS(7, R.string.GasStations, R.drawable.ic_local_gas_station_white, R.color.category_gas_station),
    HOTELS(8, R.string.Hotels_Hostels, R.drawable.ic_home_white, R.color.category_accomodation),
    MARKETS(9, R.string.Markets_Kiosks, R.drawable.ic_store_white_24dp, R.color.category_market),
    MOBILE_STORES(10, R.string.MobilePhoneStore, R.drawable.ic_phonelink_setup_white, R.color.category_mobile_phone_store),
    PARKING(11, R.string.Parking, R.drawable.ic_local_parking, R.color.category_parking),
    PHARMACIES(12, R.string.Pharmacy_Healthcare, R.drawable.ic_local_doctor_white, R.color.category_pharmacy),
    POST_OFFICES(13, R.string.Post_Offices, R.drawable.ic_local_post_office, R.color.category_post_office),
    RESTAURANTS(14, R.string.Restaurants, R.drawable.ic_restaurant_white, R.color.category_restaurant),
    SHOPPING(15, R.string.Shopping_Malls, R.drawable.ic_local_mall_white_24dp, R.color.category_shopping_mall),
    TOURISM(16, R.string.Tourism, R.drawable.ic_nature_people_white, R.color.category_tourism),
    TRANSPORTATION(17, R.string.Transportation, R.drawable.ic_local_transit_station_white, R.color.category_transportation);

    private static final Map<Integer, CategoryDisplayConfig> reverseMap = new HashMap<>();

    static {
        CategoryDisplayConfig[] values = CategoryDisplayConfig.values();
        for (CategoryDisplayConfig config : values) {
            reverseMap.put(config.id, config);
        }
    }

    public int id;
    @StringRes
    public int title;
    @DrawableRes
    public int icon;
    @ColorRes
    public int color;

    CategoryDisplayConfig(int id, @StringRes int title, @DrawableRes int icon, @ColorRes int color) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.color = color;
    }

    public static CategoryDisplayConfig getById(int key) {
        if (reverseMap.containsKey(key)) {
            return reverseMap.get(key);
        } else {
            return UNDEFINED;
        }
    }
}
