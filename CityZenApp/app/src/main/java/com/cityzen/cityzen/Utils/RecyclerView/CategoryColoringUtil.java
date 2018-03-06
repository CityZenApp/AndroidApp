package com.cityzen.cityzen.Utils.RecyclerView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.FilterCategory;
import com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter.OsmTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // special definition for cuisine
        customIconMapping.put("pizza", R.drawable.ic_cuisine_pizza);
        customIconMapping.put("burger", R.drawable.ic_cuisine_hamburger);
    }

    public static void setupPlaceIcon(Context context, String type, ImageView categoryImageView) {
        if (iconMapping.containsKey(type)) {
            setupItemIcon(context, iconMapping.get(type), categoryImageView);
        } else {
            setupItemIcon(context, CategoryDisplayConfig.UNDEFINED, categoryImageView);
        }
    }

    public static void setupPlaceIcon(Context context, String type, String cuisine, ImageView categoryImageView) {
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

    private class CustomCategoryConfig {
        public @DrawableRes int icon;

        private CustomCategoryConfig(@ColorRes int icon) {
            this.icon = icon;
        }
    }
}
