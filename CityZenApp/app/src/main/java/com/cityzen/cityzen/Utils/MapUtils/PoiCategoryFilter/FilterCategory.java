package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.util.ArrayList;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class FilterCategory {

    /**
     * <!--Points Of interest-->
     * <array name="poi_titles"> <!--Localization needed-->
     * <item>Cafés</item>
     * <item>Bars &amp; clubs</item>
     * <item>Restaurants</item>
     * <item>Fast food</item>
     * <item>Tourism</item>
     * <item>Hotels &amp; hostels</item>
     * <item>Transportation</item>
     * <item>Gas stations</item>
     * <item>Pharmacies &amp; healthcare</item>
     * <item>Banks, ATMs &amp; exchange</item>
     * <item>Mobile phone stores</item>
     * <item>Markets &amp; kiosks</item>
     * <item>Shopping malls</item>
     * <item>Parking</item>
     * <item>Rent a bicycle</item>
     * <item>Rent a car &amp; car sharing</item>
     * </array>
     * <p>
     * <array name="poi_id"> <!--Don't localize, used for app functionality-->
     * <item>0</item>
     * <item>1</item>
     * <item>2</item>
     * <item>3</item>
     * <item>4</item>
     * <item>5</item>
     * <item>6</item>
     * <item>7</item>
     * <item>8</item>
     * <item>9</item>
     * <item>10</item>
     * <item>11</item>
     * <item>12</item>
     * <item>13</item>
     * <item>14</item>
     * <item>15</item>
     * </array>
     */

    public static ArrayList<OsmTag> getFilters(int poiId) {
        ArrayList<OsmTag> tags = new ArrayList<>();
        switch (poiId) {
            case 0:
                tags.add(new OsmTag("amenity", "cafe"));
                tags.add(new OsmTag("shop", "coffee"));
                break;
            case 1:
                tags.add(new OsmTag("amenity", "bar"));
                tags.add(new OsmTag("amenity", "nightclub"));
                tags.add(new OsmTag("amenity", "pub"));
                break;
            case 2:
                tags.add(new OsmTag("amenity", "restaurant"));
                tags.add(new OsmTag("amenity", "food_court"));
                break;
            case 3:
                tags.add(new OsmTag("amenity", "fast_food"));
                break;
            case 4:
                tags.add(new OsmTag("tourism", "museum"));
                tags.add(new OsmTag("tourism", "information"));
                tags.add(new OsmTag("tourism", "attraction"));
                tags.add(new OsmTag("tourism", "yes"));
                tags.add(new OsmTag("historic", "monument"));
                tags.add(new OsmTag("tourism", "zoo"));
                break;
            case 5:
                tags.add(new OsmTag("building", "hotel"));
                tags.add(new OsmTag("tourism", "hotel"));
                tags.add(new OsmTag("tourism", "apartment"));
                tags.add(new OsmTag("tourism", "hostel"));
                tags.add(new OsmTag("tourism", "motel"));
                tags.add(new OsmTag("tourism", "guest_house"));
                break;
            case 6:
                tags.add(new OsmTag("amenity", "taxi"));
                tags.add(new OsmTag("amenity", "bus_station"));
                tags.add(new OsmTag("railway", "station"));
                tags.add(new OsmTag("highway", "bus_stop"));
                tags.add(new OsmTag("railway", "tram_stop"));
                tags.add(new OsmTag("public_transport", "station"));
                tags.add(new OsmTag("railway", "subway_entrance"));
                break;
            case 7:
                tags.add(new OsmTag("amenity", "fuel"));
                tags.add(new OsmTag("shop", "fuel"));
                break;
            case 8:
                tags.add(new OsmTag("amenity", "clinic"));
                tags.add(new OsmTag("amenity", "doctors"));
                tags.add(new OsmTag("amenity", "dentist"));
                tags.add(new OsmTag("amenity", "pharmacy"));
                tags.add(new OsmTag("amenity", "hospital"));
                tags.add(new OsmTag("shop", "herbalist"));
                tags.add(new OsmTag("healthcare", "alternative"));
                break;
            case 9:
                tags.add(new OsmTag("amenity", "atm"));
                tags.add(new OsmTag("amenity", "bank"));
                tags.add(new OsmTag("amenity", "bureau_de_change"));
                break;
            case 10:
                tags.add(new OsmTag("shop", "mobile_phone"));
                break;
            case 11:
                tags.add(new OsmTag("amenity", "marketplace"));
                tags.add(new OsmTag("building", "retail"));
                tags.add(new OsmTag("building", "kiosks"));
                tags.add(new OsmTag("shop", "convenience"));
                tags.add(new OsmTag("shop", "kiosks"));
                tags.add(new OsmTag("shop", "supermarket"));
                tags.add(new OsmTag("shop", "general"));
                tags.add(new OsmTag("shop", "variety_store"));
                tags.add(new OsmTag("shop", "tabacco"));
                break;
            case 12:
                tags.add(new OsmTag("shop", "department_store"));
                tags.add(new OsmTag("shop", "mall"));
                break;
            case 13:
                tags.add(new OsmTag("amenity", "parking"));
                tags.add(new OsmTag("amenity", "parking_space"));
                tags.add(new OsmTag("building", "parking"));
                break;
            case 14:
                tags.add(new OsmTag("amenity", "bicycle_rental"));
                break;
            case 15:
                tags.add(new OsmTag("amenity", "car_rental"));
                tags.add(new OsmTag("amenity", "car_sharing"));
                break;
            default:
                break;
        }
        return tags;
    }
}
