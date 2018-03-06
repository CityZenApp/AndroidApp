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

    static {
        Map<String, List<OsmTag>> categories = FilterCategory.getAllFilters();
        Set<String> keys = categories.keySet();
        CategoryDisplayConfig categoryDisplayConfig;
        for (String key : keys) {
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
        iconMapping.put("university", CategoryDisplayConfig.BUILDINGS);
        iconMapping.put("pedestrian", CategoryDisplayConfig.TOURISM);
        iconMapping.put("mobile_shop", CategoryDisplayConfig.MOBILE_STORES);
    }

    public static void setupPlaceIcon(Context context, String type, ImageView categoryImageView) {
        if (iconMapping.containsKey(type)) {
            setupItemIcon(context, iconMapping.get(type), categoryImageView);
        } else {
            setupItemIcon(context, CategoryDisplayConfig.UNDEFINED, categoryImageView);
        }
    }

    private static void setupItemIcon(Context context, CategoryDisplayConfig displayConfig, ImageView categoryImageView) {
        categoryImageView.setImageResource(displayConfig.icon);
        categoryImageView.getBackground().setColorFilter(ContextCompat.getColor(context, displayConfig.color), PorterDuff.Mode.SRC_IN);
    }
}
