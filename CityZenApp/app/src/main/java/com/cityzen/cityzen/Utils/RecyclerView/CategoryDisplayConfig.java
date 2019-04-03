package com.cityzen.cityzen.Utils.RecyclerView;

import com.cityzen.cityzen.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

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
    HOTELS(8, R.string.Hotels_Hostels, R.drawable.ic_local_hotel, R.color.category_accomodation),
    MARKETS(9, R.string.Markets_Kiosks, R.drawable.ic_store_white_24dp, R.color.category_market),
    MOBILE_STORES(10, R.string.MobilePhoneStore, R.drawable.ic_phonelink_setup_white, R.color.category_mobile_phone_store),
    PARKING(11, R.string.Parking, R.drawable.ic_local_parking, R.color.category_parking),
    HEALTH_SERVICES(12, R.string.Pharmacy_Healthcare, R.drawable.ic_local_doctor_white, R.color.category_pharmacy),
    POST_OFFICES(13, R.string.Post_Offices, R.drawable.ic_local_post_office, R.color.category_post_office),
    RESTAURANTS(14, R.string.Restaurants, R.drawable.ic_restaurant_white, R.color.category_restaurant),
    SHOPPING(15, R.string.Shopping_Malls, R.drawable.ic_local_mall_white_24dp, R.color.category_shopping_mall),
    TOURISM(16, R.string.Tourism, R.drawable.ic_nature_people_white, R.color.category_tourism),
    TRANSPORTATION(17, R.string.Transportation, R.drawable.ic_local_transit_station_white, R.color.category_transportation),

    LIBRARY(-1000, R.string.Location, R.drawable.ic_local_library, R.color.category_building),
    PHOTO(-1001, R.string.Location, R.drawable.ic_local_camera, R.color.category_shopping_mall),
    JEWELERY(-1002, R.string.Location, R.drawable.ic_local_jewelery, R.color.category_shopping_mall),
    OPTICIAN(-1003, R.string.Location, R.drawable.ic_local_optician, R.color.category_shopping_mall),
    PUB(-1004, R.string.Location, R.drawable.ic_local_pub, R.color.category_bar),
    CONVENIENCE(-1005, R.string.Location, R.drawable.ic_local_convenience, R.color.category_market),
    SUPERMARKET(-1006, R.string.Location, R.drawable.ic_local_cart, R.color.category_market),
    HAIRDRESSER(-1007, R.string.Location, R.drawable.ic_local_hairdresser, R.color.category_shopping_mall),
    CINEMA(-1008, R.string.Location, R.drawable.ic_local_cinema, R.color.category_shopping_mall),
    PET(-1009, R.string.Location, R.drawable.ic_local_pet, R.color.category_shopping_mall),
    DEPARTMENT_STORE(-1010, R.string.Location, R.drawable.ic_local_department_store, R.color.category_shopping_mall),
    WINE(-1011, R.string.Location, R.drawable.ic_local_wine, R.color.category_bar),
    HIFI(-1012, R.string.Location, R.drawable.ic_local_hifi, R.color.category_shopping_mall),
    SHOES(-1013, R.string.Location, R.drawable.ic_local_shoes, R.color.category_shopping_mall),
    FLORIST(-1014, R.string.Location, R.drawable.ic_local_florist, R.color.category_shopping_mall),
    CLOTHING(-1015, R.string.Location, R.drawable.ic_local_clothing, R.color.category_shopping_mall),
    LAUNDRY(-1016, R.string.Location, R.drawable.ic_local_laundry, R.color.category_shopping_mall),
    STATIONERY(-1017, R.string.Location, R.drawable.ic_local_stationery, R.color.category_shopping_mall),
    CHEMIST(-1018, R.string.Location, R.drawable.ic_local_chemist, R.color.category_shopping_mall),
    NEWSAGENT(-1019, R.string.Location, R.drawable.ic_local_newspaper, R.color.category_market),
    THEATRE(-1020, R.string.Location, R.drawable.ic_local_theater, R.color.category_building),
    FOUNTAIN(-1021, R.string.Location, R.drawable.ic_local_fountain, R.color.category_tourism),
    ART(-1022, R.string.Location, R.drawable.ic_local_art, R.color.category_shopping_mall),
    COSMETICS(-1023, R.string.Location, R.drawable.ic_local_cosmetics, R.color.category_shopping_mall),
    BUTCHER(-1024, R.string.Location, R.drawable.ic_local_butcher, R.color.category_shopping_mall),
    PARFUMERY(-1025, R.string.Location, R.drawable.ic_local_parfumery, R.color.category_shopping_mall),
    ALCOHOL(-1026, R.string.Location, R.drawable.ic_local_alcohol, R.color.category_market),
    BEVERAGES(-1027, R.string.Location, R.drawable.ic_local_beverages, R.color.category_market),
    PHARMACIES(-1028, R.string.Location, R.drawable.ic_local_pharmacy_white, R.color.category_pharmacy),
    MUSEUM(-1029, R.string.Location, R.drawable.ic_local_museum, R.color.category_tourism),
    MONUMENT(-1030, R.string.Location, R.drawable.ic_local_monument, R.color.category_tourism),
    EMBASSY(-1030, R.string.Location, R.drawable.ic_local_embassy, R.color.category_building),
    BICYCLE_SHOP(-1031, R.string.Location, R.drawable.ic_local_bike, R.color.category_shopping_mall),
    HOSTEL(-1032, R.string.Location, R.drawable.ic_local_hostel, R.color.category_accomodation),
    POLICE(-1033, R.string.Location, R.drawable.ic_local_police, R.color.category_building),
    FIREFIGHTER(-1034, R.string.Location, R.drawable.ic_local_fire_station, R.color.category_building),
    DENTIST(-1035, R.string.Location, R.drawable.ic_local_dentist, R.color.category_pharmacy),
    NIGHTCLUB(-1036, R.string.Location, R.drawable.ic_local_music, R.color.category_bar);

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
