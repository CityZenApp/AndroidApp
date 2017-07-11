package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;

import java.util.List;
import java.util.Map;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    private Context context;
    private List<Place> data;// this is a temp file fof preview purposes

    public PlaceListAdapter(Context context, List<Place> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_list_item, parent, false);
        PlaceListAdapter.ViewHolder holder = new PlaceListAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaceListAdapter.ViewHolder holder, int position) {
        boolean hasOpeningHours = false;
        holder.title.setText(data.get(position).getDisplayName());
        Map<String, String> tags = data.get(position).getTags();
        if (tags != null)
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                if (tag.getKey().equals("opening_hours")) {
                    holder.openingHours.setText(tag.getValue());
                    hasOpeningHours = true;
                }
            }
        setupPlaceIcon(data.get(position).getType(), holder);
        if (hasOpeningHours)
            holder.openingHours.setVisibility(View.VISIBLE);
        else
            holder.openingHours.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void resetAdapter(List<Place> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public void resetAdapter() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView title;
        TextView openingHours;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.placeTitle);
            coverImage = (ImageView) itemView.findViewById(R.id.placeImage);
            openingHours = (TextView) itemView.findViewById(R.id.placeOpeningHours);
        }
    }

    private void setupPlaceIcon(String type, PlaceListAdapter.ViewHolder holder) {
        switch (type) {
            case "bar":
                holder.coverImage.setImageResource(R.drawable.search_cafe);
                break;
            case "cafe":
                holder.coverImage.setImageResource(R.drawable.search_cafe);
                break;
            case "pub":
                holder.coverImage.setImageResource(R.drawable.search_ic_location);
                break;
            case "restaurant":
                holder.coverImage.setImageResource(R.drawable.search_restaurant);
                break;
            case "city":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "administrative":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "mall":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "residential":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "bank":
                holder.coverImage.setImageResource(R.drawable.search_ic_local_atm);
                break;
            case "locality":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "village":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "university":
                holder.coverImage.setImageResource(R.drawable.ic_location_city_black_24dp);
                break;
            case "supermarket":
                holder.coverImage.setImageResource(R.drawable.search_ic_home);
                break;
            case "hotel":
                holder.coverImage.setImageResource(R.drawable.search_ic_home);
                break;
            case "hostel":
                holder.coverImage.setImageResource(R.drawable.search_ic_home);
                break;
            case "guest_house":
                holder.coverImage.setImageResource(R.drawable.search_ic_home);
                break;
            case "attraction":
                holder.coverImage.setImageResource(R.drawable.search_ic_nature_people);
                break;
            case "pedestrian":
                holder.coverImage.setImageResource(R.drawable.search_ic_nature_people);
                break;
            case "monument":
                holder.coverImage.setImageResource(R.drawable.search_ic_nature_people);
                break;
            case "atm":
                holder.coverImage.setImageResource(R.drawable.search_ic_local_atm);
                break;
            case "taxi":
                holder.coverImage.setImageResource(R.drawable.search_transit_station);
                break;
            case "bus_stop":
                holder.coverImage.setImageResource(R.drawable.search_transit_station);
                break;
            case "bus_station":
                holder.coverImage.setImageResource(R.drawable.search_transit_station);
                break;
            case "transportation":
                holder.coverImage.setImageResource(R.drawable.search_transit_station);
                break;
            case "fuel":
                holder.coverImage.setImageResource(R.drawable.search_gas_station);
                break;
            case "hospital":
                holder.coverImage.setImageResource(R.drawable.search_doctor);
                break;
            case "pharmacy":
                holder.coverImage.setImageResource(R.drawable.search_doctor);
                break;
            case "mobile_phone":
                holder.coverImage.setImageResource(R.drawable.search_ic_phonelink_setup);
                break;
            case "mobile_shop":
                holder.coverImage.setImageResource(R.drawable.search_ic_phonelink_setup);
                break;
            case "fast_food":
                holder.coverImage.setImageResource(R.drawable.search_ic_room_service);
                break;
            default:
                holder.coverImage.setImageResource(R.drawable.search_ic_location);
                break;
        }
    }
}
