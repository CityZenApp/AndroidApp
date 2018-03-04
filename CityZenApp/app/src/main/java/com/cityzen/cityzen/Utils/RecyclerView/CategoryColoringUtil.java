package com.cityzen.cityzen.Utils.RecyclerView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.cityzen.cityzen.R;

/**
 * Utilities implementation for coloring category image view items.
 */
public abstract class CategoryColoringUtil {
    public static void setupPlaceIcon(Context context, String type, ImageView categoryImageView) {
        switch (type) {
            case "bar":
                setupItemIcon(context, R.drawable.ic_local_bar_white_24dp, R.color.category_bar, categoryImageView);
                break;
            case "cafe":
                setupItemIcon(context, R.drawable.ic_local_cafe_white, R.color.category_cafe, categoryImageView);
                break;
            case "pub":
                setupItemIcon(context, R.drawable.ic_map_marker, R.color.category_building, categoryImageView);
                break;
            case "restaurant":
                setupItemIcon(context, R.drawable.ic_restaurant_white, R.color.category_restaurant, categoryImageView);
                break;
            case "city":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "administrative":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "mall":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_shopping_mall, categoryImageView);
                break;
            case "residential":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "bank":
                setupItemIcon(context, R.drawable.ic_local_atm_white, R.color.category_atm, categoryImageView);
                break;
            case "locality":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "village":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "university":
                setupItemIcon(context, R.drawable.ic_local_city, R.color.category_building, categoryImageView);
                break;
            case "hotel":
                setupItemIcon(context, R.drawable.ic_home_white, R.color.category_accomodation, categoryImageView);
                break;
            case "hostel":
                setupItemIcon(context, R.drawable.ic_home_white, R.color.category_accomodation, categoryImageView);
                break;
            case "guest_house":
                setupItemIcon(context, R.drawable.ic_home_white, R.color.category_accomodation, categoryImageView);
                break;
            case "attraction":
                setupItemIcon(context, R.drawable.ic_nature_people_white, R.color.category_tourism, categoryImageView);
                break;
            case "pedestrian":
                setupItemIcon(context, R.drawable.ic_nature_people_white, R.color.category_tourism, categoryImageView);
                break;
            case "monument":
                setupItemIcon(context, R.drawable.ic_nature_people_white, R.color.category_tourism, categoryImageView);
                break;
            case "atm":
                setupItemIcon(context, R.drawable.ic_local_atm_white, R.color.category_atm, categoryImageView);
                break;
            case "taxi":
                setupItemIcon(context, R.drawable.ic_local_transit_station_white, R.color.category_transportation, categoryImageView);
                break;
            case "bus_stop":
                setupItemIcon(context, R.drawable.ic_local_transit_station_white, R.color.category_transportation, categoryImageView);
                break;
            case "bus_station":
                setupItemIcon(context, R.drawable.ic_local_transit_station_white, R.color.category_transportation, categoryImageView);
                break;
            case "transportation":
                setupItemIcon(context, R.drawable.ic_local_transit_station_white, R.color.category_transportation, categoryImageView);
                break;
            case "fuel":
                setupItemIcon(context, R.drawable.ic_local_gas_station_white, R.color.category_gas_station, categoryImageView);
                break;
            case "hospital":
                setupItemIcon(context, R.drawable.ic_local_doctor_white, R.color.category_pharmacy, categoryImageView);
                break;
            case "pharmacy":
                setupItemIcon(context, R.drawable.ic_local_pharmacy_white, R.color.category_pharmacy, categoryImageView);
                break;
            case "mobile_phone":
                setupItemIcon(context, R.drawable.ic_phonelink_setup_white, R.color.category_mobile_phone_store, categoryImageView);
                break;
            case "mobile_shop":
                setupItemIcon(context, R.drawable.ic_phonelink_setup_white, R.color.category_mobile_phone_store, categoryImageView);
                break;
            case "fast_food":
                setupItemIcon(context, R.drawable.ic_room_service_white_24dp, R.color.category_fast_food, categoryImageView);
                break;
            case "supermarket":
                setupItemIcon(context, R.drawable.ic_store_white_24dp, R.color.category_market, categoryImageView);
                break;
            default:
                setupItemIcon(context, R.drawable.ic_map_marker, R.color.category_building, categoryImageView);
                break;
        }
    }

    private static void setupItemIcon(Context context, @DrawableRes int icon, @ColorRes int backgroundColor, ImageView categoryImageView) {
        categoryImageView.setImageResource(icon);
        categoryImageView.getBackground().setColorFilter(ContextCompat.getColor(context, backgroundColor), PorterDuff.Mode.SRC_IN);
    }
}
