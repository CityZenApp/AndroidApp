package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.util.ArrayList;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class FilterCategory {

    /**
     * <!--Points Of interest-->
     * <array name="poi_titles">
     * <item>Bars/Cafes</item>
     * <item>Hotel/Hostel</item>
     * <item>Tourist attractions</item>
     * <item>Bank/Atm</item>
     * <item>Transportation</item>
     * <item>Gas stations</item>
     * <item>Drugstores</item>
     * <item>Mobile phone store</item>
     * <item>Restaurant/FastFood</item>
     * </array>
     * <p>
     * <array name="poi_id">
     * <item>0</item>
     * <item>1</item>
     * <item>2</item>
     * <item>3</item>
     * <item>4</item>
     * <item>5</item>
     * <item>6</item>
     * <item>7</item>
     * <item>8</item>
     * </array>
     */
   /* public static ArrayList<OsmTag> getFilters(int poiId) {
        ArrayList<OsmTag> tags = new ArrayList<>();
        switch (poiId) {
            case 0://Bars/Cafes
                tags.add(new OsmTag("amenity", "bar"));
                tags.add(new OsmTag("amenity", "cafe"));
                break;
            case 1://Hotel/Hostel
                tags.add(new OsmTag("tourism", "hotel"));
                tags.add(new OsmTag("tourism", "hostel"));
                tags.add(new OsmTag("tourism", "guest_house"));
                break;
            case 2://Tourist attractions
                tags.add(new OsmTag("tourism", "attraction"));
                tags.add(new OsmTag("historic", "monument"));
                break;
            case 3://Bank/Atm
                tags.add(new OsmTag("amenity", "bank"));
                tags.add(new OsmTag("amenity", "atm"));
                break;
            case 4://Transportation
                tags.add(new OsmTag("building", "transportation"));
                tags.add(new OsmTag("amenity", "taxi"));
                tags.add(new OsmTag("amenity", "bus_station"));
                break;
            case 5://Gas stations
                tags.add(new OsmTag("amenity", "fuel"));
                tags.add(new OsmTag("amenity", "parking"));
                break;
            case 6://Drugstores
                tags.add(new OsmTag("amenity", "pharmacy"));
                tags.add(new OsmTag("amenity", "hospital"));
                break;
            case 7://Mobile phone store
                tags.add(new OsmTag("shop", "mobile_phone"));
                break;
            case 8://Restaurant/FastFood
                tags.add(new OsmTag("amenity", "restaurant"));
                tags.add(new OsmTag("amenity", "fast_food"));
                break;
//            case 9:
//                tags.add(new OsmTag("amenity", "fast_food"));
//                break;
            default:
                break;
        }
        return tags;
    }
   */
    public static ArrayList<OsmTag> getFilters(int poiId) {
        ArrayList<OsmTag> tags = new ArrayList<>();
        switch (poiId) {
            case 0:
                tags.add(new OsmTag("amenity", "cafe"));
                break;
            case 1:
                tags.add(new OsmTag("amenity", "bar"));
                break;
            case 2:
                tags.add(new OsmTag("amenity", "restaurant"));
                break;
            case 3:
                tags.add(new OsmTag("amenity", "fast_food"));
                break;
            case 4:
                tags.add(new OsmTag("tourism", "attraction"));
                tags.add(new OsmTag("historic", "monument"));
                break;
            case 5:
                tags.add(new OsmTag("tourism", "hotel"));
                tags.add(new OsmTag("tourism", "hostel"));
                tags.add(new OsmTag("tourism", "guest_house"));
                break;
            case 6:
                tags.add(new OsmTag("building", "transportation"));
                tags.add(new OsmTag("amenity", "taxi"));
                tags.add(new OsmTag("amenity", "bus_station"));
                break;
            case 7:
                tags.add(new OsmTag("amenity", "fuel"));
                break;
            case 8:
                tags.add(new OsmTag("amenity", "pharmacy"));
                tags.add(new OsmTag("amenity", "hospital"));
                break;
            case 9:
                tags.add(new OsmTag("amenity", "bank"));
                tags.add(new OsmTag("amenity", "atm"));
                break;
            default:
                break;
        }
        return tags;
    }
}
