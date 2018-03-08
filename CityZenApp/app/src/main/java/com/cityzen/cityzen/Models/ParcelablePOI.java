package com.cityzen.cityzen.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;

import java.util.Map;

import info.metadude.java.library.overpass.models.Element;

/**
 * This class is used as a middle ground PO-JO object to convert both
 * {@link Element} and {@link Place} object into a parcelable object
 * to pass between fragments and store in shared preferences.
 *
 * @author Valdio Veliu
 */
public class ParcelablePOI implements Parcelable {

    /*
     * Element
     *
     * {type='node', id=2608288886, lat=41.3296753, lon=19.8145024,
     * tags={addr:city=Tirana, addr:street=Rruga e Durresit, amenity=bar, name=Kafe Flora, wheelchair=limited}}
     */

    /**
     * Place -- generated on search
     * <p>
     * <p>
     * [{"place_id":"27815478","licence":"Data © OpenStreetMap contributors,
     * ODbL 1.0. http:\/\/www.openstreetmap.org\/copyright",
     * "osm_type":"node","osm_id":"2608288886",
     * "boundingbox":["41.3296253","41.3297253","19.8144524","19.8145524"],"lat":"41.3296753","lon":"19.8145024",
     * "display_name":"Kafe Flora, Rruga e Durrësit, Lapraka, Tiranë, Tirana, Tirana County, Berat County, 1031, Albania",
     * "class":"amenity","type":"bar","importance":0.201,
     * "icon":"http:\/\/nominatim.openstreetmap.org\/images\/mapicons\/food_bar.p.20.png","extratags":{"wheelchair":"limited"}}]
     */

    private String poiName;             //POI name
    private String fullName = "";       //POI name + address
    private String osmType = "";        //e.g. node
    private String poiClassName = "";   //e.g. amenity
    private String poiClassType = "";   //e.g. bar
    private long id;                    //id of the node in OpenStreetMap
    private double lat;                 //latitude
    private double lon;                 //longitude
    private Map<String, String> tags;   //node tags

    public ParcelablePOI(Place place) {
        this.id = place.getOsmId();
        if (place.getDisplayName().contains(","))
            this.poiName = place.getDisplayName().substring(0, place.getDisplayName().indexOf(","));
        else
            this.poiName = place.getDisplayName();
        this.fullName = place.getDisplayName();
        this.lat = place.getLatitude();
        this.lon = place.getLongitude();
        this.tags = place.getTags();
        this.osmType = place.getOsmType();
        this.poiClassName = place.getEntityClass();
        this.poiClassType = place.getType();
    }

    public ParcelablePOI(long nodeId, String poiName, String fullName, String osmType,
                         String poiClassName, String poiClassType, double latitude, double longitude, Map<String, String> tags) {
        this.id = nodeId;
        this.poiName = poiName;
        this.fullName = fullName;
        this.lat = latitude;
        this.lon = longitude;
        this.tags = tags;
        this.osmType = osmType;
        this.poiClassName = poiClassName;
        this.poiClassType = poiClassType;
    }

    public ParcelablePOI(Element element) {
        this.id = element.id;
        this.osmType = element.type;
        this.lat = element.lat;
        this.lon = element.lon;
        this.tags = element.tags;
        boolean hasName = false;
        if (tags != null) {
            if (tags.containsKey("name")) {
                this.poiName = tags.get("name");
                this.fullName = tags.get("name");
                hasName = true;
            }
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                /*
                 * If added more tags to filter in
                 * {@link com.openstreetmap.opencity.opencity.Utils.MapUtils.PoiCategoryFilter.FilterCategory}
                 * add the new tags in the if statement
                 */
                if (tag.getKey().equals("amenity") || tag.getKey().equals("tourism") || tag.getKey().equals("historic") || tag.getKey().equals("building") || tag.getKey().equals("shop")) {
                    this.poiClassName = tag.getKey();
                    this.poiClassType = tag.getValue();
                }
            }

            //filter ATM names, set name to me the Atm operator
            if (tags.containsKey("amenity") && "atm".equals(tags.get("amenity")) && !hasName) {
                if (tags.containsKey("operator")) {
                    this.poiName = tags.get("operator");
                    this.fullName = tags.get("operator");
                }
            }

            // extract tags for fullName
            if (tags.containsKey("addr:street")) {
                if (fullName.equals("")) {
                    fullName = tags.get("addr:street");
                } else {
                    fullName = fullName + ", " + tags.get("addr:street");
                }
            }
            if (tags.containsKey("addr:city")) {
                if (fullName.equals("")) {
                    fullName = tags.get("addr:city");
                } else {
                    fullName = fullName + ", " + tags.get("addr:city");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Name: " + this.poiName + "\n"
                + "FullName: " + this.fullName + "\n"
                + "ID: " + this.id + "\n"
                + "OSM TYPE: " + this.osmType + "\n"
                + "ClassName: " + this.poiClassName + "\n"
                + "ClassType: " + this.poiClassType + "\n"
                + "Latitude: " + this.lat + "\n"
                + "Longitude: " + this.lon + "\n"
                + "Tags: " + this.tags;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOsmType() {
        return osmType;
    }

    public void setOsmType(String osmType) {
        this.osmType = osmType;
    }

    public String getPoiClassName() {
        return poiClassName;
    }

    public void setPoiClassName(String poiClassName) {
        this.poiClassName = poiClassName;
    }

    public String getPoiClassType() {
        return poiClassType;
    }

    public void setPoiClassType(String poiClassType) {
        this.poiClassType = poiClassType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return lon;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<ParcelablePOI> CREATOR
            = new Parcelable.Creator<ParcelablePOI>() {
        public ParcelablePOI createFromParcel(Parcel source) {
            return new ParcelablePOI(source);
        }

        public ParcelablePOI[] newArray(int size) {
            return new ParcelablePOI[size];
        }
    };

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(poiName);
        dest.writeString(fullName);
        dest.writeString(osmType);
        dest.writeString(poiClassName);
        dest.writeString(poiClassType);
        dest.writeLong(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);

        if (tags != null) {
            //write tags to parcel
            final int N = tags.size();
            dest.writeInt(N);
            if (N > 0) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    dest.writeString(entry.getKey());
                    dest.writeString(entry.getKey());
                }
            }
        }
    }


    private ParcelablePOI(Parcel source) {
        poiName = source.readString();
        fullName = source.readString();
        osmType = source.readString();
        poiClassName = source.readString();
        poiClassType = source.readString();
        id = source.readLong();
        lat = source.readDouble();
        lon = source.readDouble();

        //read tags Map
        final int N = source.readInt();
        for (int i = 0; i < N; i++) {
            String key = source.readString();
            String value = source.readString();
            tags.put(key, value);
        }
    }
}
