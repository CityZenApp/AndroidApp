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
import com.cityzen.cityzen.Utils.RecyclerView.CategoryColoringUtil;

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

        CategoryColoringUtil.setupPlaceIcon(
                context, data.get(position).getPoiClassType(), holder.coverImage
        );
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
}
