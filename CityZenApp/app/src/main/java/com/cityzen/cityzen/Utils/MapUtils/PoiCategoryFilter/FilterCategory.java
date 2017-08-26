package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.util.ArrayList;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class FilterCategory {

    /**
     * <!--Points Of interest-->
     * <array name="poi_titles"> <!--Localization needed-->
     * <item>Cafes</item>
     * <item>Bars/Clubs</item>
     * <item>Restaurants</item>
     * <item>Fast food</item>
     * <item>Tourist attractions</item>
     * <item>Hotels/Hostels</item>
     * <item>Public transport</item>
     * <item>Gas stations</item>
     * <item>Drugstores\nHealthcare centers</item>
     * <item>Banks/ATMs</item>
     * <item>Mobile phone store</item>
     * <item>Super Markets</item>
     * <item>Shopping Malls</item> <!-- http://wiki.openstreetmap.org/wiki/Tag:shop%3Dmall-->
     * <item>Parkings</item>
     * <item>Rent a bicycle</item>
     * <item>Rent a car &amp; Care sharing</item>
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
            case 10:
                tags.add(new OsmTag("shop", "mobile_phone"));
                break;
            case 11:
                tags.add(new OsmTag("shop", "supermarket"));
                break;
            case 12:
                tags.add(new OsmTag("shop", "mall"));
                break;
            case 13:
                tags.add(new OsmTag("amenity", "parking"));
                tags.add(new OsmTag("amenity", "parking_space"));
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
