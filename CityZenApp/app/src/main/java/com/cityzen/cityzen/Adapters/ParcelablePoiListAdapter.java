package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */
public class ParcelablePoiListAdapter extends RecyclerView.Adapter<ParcelablePoiListAdapter.ViewHolder> {

    private Context context;
    private List<ParcelablePOI> data;// this is a temp file fof preview purposes

    public ParcelablePoiListAdapter(Context context, List<ParcelablePOI> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list_item, parent, false);
       return new ParcelablePoiListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ParcelablePoiListAdapter.ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getPoiName());
        Map<String, String> tags = data.get(position).getTags();
        if (tags != null) {
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                if (tag.getKey().equals("opening_hours"))
                    holder.openingHours.setText(tag.getValue());
            }
        }

        setupPlaceIcon(data.get(position).getPoiClassType(), holder);
        if (holder.openingHours.getText() != null && !holder.openingHours.getText().equals("")) {
            holder.openingHours.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView title;
        TextView openingHours;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.placeTitle);
            coverImage = itemView.findViewById(R.id.placeImage);
            openingHours = itemView.findViewById(R.id.placeOpeningHours);
        }
    }

    private void setupPlaceIcon(String type, ParcelablePoiListAdapter.ViewHolder holder) {
        switch (type) {
            case "bar":
                setupItemIcon(R.drawable.ic_local_bar_white_24dp, R.color.category_bar_cafe, holder.coverImage);
                break;
            case "cafe":
                setupItemIcon(R.drawable.ic_local_cafe_white, R.color.category_bar_cafe, holder.coverImage);
                break;
            case "pub":
                setupItemIcon(R.drawable.ic_map_marker, R.color.category_building, holder.coverImage);
                break;
            case "restaurant":
                setupItemIcon(R.drawable.ic_restaurant_white, R.color.category_restaurant, holder.coverImage);
                break;
            case "city":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "administrative":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "mall":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "residential":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "bank":
                setupItemIcon(R.drawable.ic_local_atm_white, R.color.category_atm, holder.coverImage);
                break;
            case "locality":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "village":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "university":
                setupItemIcon(R.drawable.ic_local_city, R.color.category_building, holder.coverImage);
                break;
            case "hotel":
                setupItemIcon(R.drawable.ic_home_white, R.color.category_home, holder.coverImage);
                break;
            case "hostel":
                setupItemIcon(R.drawable.ic_home_white, R.color.category_home, holder.coverImage);
                break;
            case "guest_house":
                setupItemIcon(R.drawable.ic_home_white, R.color.category_home, holder.coverImage);
                break;
            case "attraction":
                setupItemIcon(R.drawable.ic_nature_people_white, R.color.category_attraction, holder.coverImage);
                break;
            case "pedestrian":
                setupItemIcon(R.drawable.ic_nature_people_white, R.color.category_attraction, holder.coverImage);
                break;
            case "monument":
                setupItemIcon(R.drawable.ic_nature_people_white, R.color.category_attraction, holder.coverImage);
                break;
            case "atm":
                setupItemIcon(R.drawable.ic_local_atm_white, R.color.category_atm, holder.coverImage);
                break;
            case "taxi":
                setupItemIcon(R.drawable.ic_local_transit_station_white, R.color.category_transit, holder.coverImage);
                break;
            case "bus_stop":
                setupItemIcon(R.drawable.ic_local_transit_station_white, R.color.category_transit, holder.coverImage);
                break;
            case "bus_station":
                setupItemIcon(R.drawable.ic_local_transit_station_white, R.color.category_transit, holder.coverImage);
                break;
            case "transportation":
                setupItemIcon(R.drawable.ic_local_transit_station_white, R.color.category_transit, holder.coverImage);
                break;
            case "fuel":
                setupItemIcon(R.drawable.ic_local_gas_station_white, R.color.category_gas, holder.coverImage);
                break;
            case "hospital":
                setupItemIcon(R.drawable.ic_local_doctor_white, R.color.category_health, holder.coverImage);
                break;
            case "pharmacy":
                setupItemIcon(R.drawable.ic_local_pharmacy_white, R.color.category_health, holder.coverImage);
                break;
            case "mobile_phone":
                setupItemIcon(R.drawable.ic_phonelink_setup_white, R.color.category_communication, holder.coverImage);
                break;
            case "mobile_shop":
                setupItemIcon(R.drawable.ic_phonelink_setup_white, R.color.category_communication, holder.coverImage);
                break;
            case "fast_food":
                setupItemIcon(R.drawable.ic_room_service_white_24dp, R.color.category_fast_food, holder.coverImage);
                break;
            case "supermarket":
                setupItemIcon(R.drawable.ic_home_white, R.color.category_home, holder.coverImage);
                break;
            default:
                setupItemIcon(R.drawable.ic_map_marker, R.color.category_building, holder.coverImage);
                break;
        }
    }

    private void setupItemIcon(@DrawableRes int icon, @ColorRes int backgroundColor, ImageView iconView) {
        iconView.setImageResource(icon);
        iconView.getBackground().setColorFilter(ContextCompat.getColor(context, backgroundColor), PorterDuff.Mode.SRC_IN);
    }

}
