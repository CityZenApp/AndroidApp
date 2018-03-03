package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;
import com.cityzen.cityzen.Utils.RecyclerView.CategoryColoringUtil;

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
        return new PlaceListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceListAdapter.ViewHolder holder, int position) {
        boolean hasOpeningHours = false;
        holder.title.setText(data.get(position).getDisplayName());
        Map<String, String> tags = data.get(position).getTags();
        if (tags != null) {
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                if (tag.getKey().equals("opening_hours")) {
                    holder.openingHours.setText(tag.getValue());
                    hasOpeningHours = true;
                }
            }
        }

        CategoryColoringUtil.setupPlaceIcon(
                context, data.get(position).getType(), holder.coverImage
        );
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
            title = itemView.findViewById(R.id.placeTitle);
            coverImage = itemView.findViewById(R.id.placeImage);
            openingHours = itemView.findViewById(R.id.placeOpeningHours);
        }
    }
}
