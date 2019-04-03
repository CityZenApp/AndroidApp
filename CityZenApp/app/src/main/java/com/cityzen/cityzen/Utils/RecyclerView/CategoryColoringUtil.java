package com.cityzen.cityzen.Utils.RecyclerView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.FilterCategory;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.OsmTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

/**
 * Utilities implementation for coloring category image view items.
 */
public abstract class CategoryColoringUtil {
    private static final Map<String, CategoryDisplayConfig> iconMapping = new HashMap<>();
    private static final Map<String, Integer> customIconMapping = new HashMap<>();

    static {
        Map<Integer, List<OsmTag>> categories = FilterCategory.getAllFilters();
        Set<Integer> keys = categories.keySet();
        CategoryDisplayConfig categoryDisplayConfig;
        for (int key : keys) {
            categoryDisplayConfig = CategoryDisplayConfig.getById(key);
            List<OsmTag> tags = categories.get(key);
            for (OsmTag tag : tags) {
                iconMapping.put(tag.getValue(), categoryDisplayConfig);
            }
        }

        // extra types which aren't assigned to a certain category (yet)
        iconMapping.put("city", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("administrative", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("residential", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("locality", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("village", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("university", CategoryDisplayConfig.SCHOOL);
        iconMapping.put("pedestrian", CategoryDisplayConfig.TOURISM);
        iconMapping.put("mobile_shop", CategoryDisplayConfig.MOBILE_STORES);

        iconMapping.put("library", CategoryDisplayConfig.LIBRARY);
        iconMapping.put("books", CategoryDisplayConfig.LIBRARY);
        iconMapping.put("photo", CategoryDisplayConfig.PHOTO);
        iconMapping.put("jewelry", CategoryDisplayConfig.JEWELERY);
        iconMapping.put("nightclub", CategoryDisplayConfig.NIGHTCLUB);
        iconMapping.put("convenience", CategoryDisplayConfig.CONVENIENCE);
        iconMapping.put("supermarket", CategoryDisplayConfig.SUPERMARKET);
        iconMapping.put("cinema", CategoryDisplayConfig.CINEMA);
        iconMapping.put("pet", CategoryDisplayConfig.PET);
        iconMapping.put("department_store", CategoryDisplayConfig.DEPARTMENT_STORE);
        iconMapping.put("hifi", CategoryDisplayConfig.HIFI);
        iconMapping.put("florist", CategoryDisplayConfig.FLORIST);
        iconMapping.put("chemist", CategoryDisplayConfig.CHEMIST);
        iconMapping.put("clothes", CategoryDisplayConfig.CLOTHING);
        iconMapping.put("newsagent", CategoryDisplayConfig.NEWSAGENT);
        iconMapping.put("stationery", CategoryDisplayConfig.STATIONERY);
        iconMapping.put("dry_cleaning", CategoryDisplayConfig.LAUNDRY);
        iconMapping.put("hairdresser", CategoryDisplayConfig.HAIRDRESSER);
        iconMapping.put("optician", CategoryDisplayConfig.OPTICIAN);
        iconMapping.put("shoes", CategoryDisplayConfig.SHOES);
        iconMapping.put("wine", CategoryDisplayConfig.WINE);
        iconMapping.put("pub", CategoryDisplayConfig.PUB);
        iconMapping.put("cosmetics", CategoryDisplayConfig.COSMETICS);
        iconMapping.put("beauty", CategoryDisplayConfig.COSMETICS);
        iconMapping.put("alcohol", CategoryDisplayConfig.ALCOHOL);
        iconMapping.put("beverages", CategoryDisplayConfig.BEVERAGES);
        iconMapping.put("pharmacy", CategoryDisplayConfig.PHARMACIES);
        iconMapping.put("butcher", CategoryDisplayConfig.BUTCHER);
        iconMapping.put("parfumery", CategoryDisplayConfig.PARFUMERY);
        iconMapping.put("theatre", CategoryDisplayConfig.THEATRE);
        iconMapping.put("art", CategoryDisplayConfig.ART);
        iconMapping.put("basin", CategoryDisplayConfig.FOUNTAIN);
        iconMapping.put("museum", CategoryDisplayConfig.MUSEUM);
        iconMapping.put("monument", CategoryDisplayConfig.MONUMENT);
        iconMapping.put("embassy", CategoryDisplayConfig.EMBASSY);
        iconMapping.put("bicycle", CategoryDisplayConfig.BICYCLE_SHOP);
        iconMapping.put("hostel", CategoryDisplayConfig.HOSTEL);
        iconMapping.put("fire_station", CategoryDisplayConfig.FIREFIGHTER);
        iconMapping.put("police", CategoryDisplayConfig.POLICE);

        // special definition for cuisine
        customIconMapping.put("pizza", R.drawable.ic_cuisine_pizza);
        customIconMapping.put("burger", R.drawable.ic_cuisine_hamburger);
        customIconMapping.put("asian", R.drawable.ic_cuisine_asian);
        customIconMapping.put("ice_cream", R.drawable.ic_local_ice_cream);
        customIconMapping.put("seafood", R.drawable.ic_local_seafood);
        customIconMapping.put("fish", R.drawable.ic_local_seafood);
    }

    public static void setupPlaceIcon(Context context, String type, ImageView categoryImageView) {
        Log.e("TYPE", type);
        if (iconMapping.containsKey(type)) {
            setupItemIcon(context, iconMapping.get(type), categoryImageView);
        } else {
            setupItemIcon(context, CategoryDisplayConfig.UNDEFINED, categoryImageView);
        }
    }

    public static void setupPlaceIcon(Context context, String type, String cuisine, ImageView categoryImageView) {
        Log.e("TYPE", type + " - " + cuisine);
        if (customIconMapping.containsKey(cuisine) && iconMapping.containsKey(type)) {
            setupItemIcon(context, customIconMapping.get(cuisine), iconMapping.get(type).color, categoryImageView);
        } else {
            setupPlaceIcon(context, type, categoryImageView);
        }
    }

    private static void setupItemIcon(Context context, CategoryDisplayConfig displayConfig, ImageView categoryImageView) {
        setupItemIcon(context, displayConfig.icon, displayConfig.color, categoryImageView);
    }

    private static void setupItemIcon(Context context, @DrawableRes int icon, @ColorRes int color, ImageView categoryImageView) {
        categoryImageView.setImageResource(icon);
        categoryImageView.getBackground().setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN);
    }
}
