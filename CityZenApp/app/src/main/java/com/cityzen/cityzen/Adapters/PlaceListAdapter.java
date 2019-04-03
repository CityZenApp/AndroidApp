package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.OpeningHours.OpeningHoursUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmTags;
import com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser.Place;
import com.cityzen.cityzen.Utils.PoiHelper;
import com.cityzen.cityzen.Utils.RecyclerView.CategoryColoringUtil;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

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
        if (data.get(position).getDisplayName().contains(",")) {
            holder.title.setText(data.get(position).getDisplayName().substring(0, data.get(position).getDisplayName().indexOf(",")));
        } else {
            holder.title.setText(data.get(position).getDisplayName());
        }
        Map<String, String> tags = data.get(position).getTags();
        boolean hasDetailData = false;

        if (tags != null) {
            // Address info
            holder.address.setText(PoiHelper.createAddressDisplayString(data.get(position)));
            if (holder.address.getText() != null && !holder.address.getText().equals("")) {
                holder.address.setVisibility(View.VISIBLE);
                hasDetailData = true;
            } else {
                holder.address.setVisibility(View.GONE);
            }

            // Opening hours
            if (tags.containsKey(OsmTags.OPENING_HOURS)) {
                if (OpeningHoursUtils.isOpenNow(tags.get(OsmTags.OPENING_HOURS))) {
                    holder.openingHours.setText(R.string.open);
                    holder.openingHours.setTextColor(context.getResources().getColor(R.color.open));
                } else {
                    holder.openingHours.setText(R.string.closed);
                    holder.openingHours.setTextColor(context.getResources().getColor(R.color.closed));
                }
            } else {
                holder.openingHours.setText(null);
            }

            if (holder.openingHours.getText() != null && !holder.openingHours.getText().equals("")) {
                holder.openingHours.setVisibility(View.VISIBLE);
            } else {
                if (hasDetailData) {
                    holder.openingHours.setVisibility(View.GONE);
                } else {
                    holder.openingHours.setText(R.string.no_info_available);
                    holder.openingHours.setTextColor(context.getResources().getColor(R.color.grey600));
                }
            }
        }

        if (data.get(position).getTags().containsKey("cuisine")) {
            CategoryColoringUtil.setupPlaceIcon(
                    context, data.get(position).getType(),
                    data.get(position).getTags().get("cuisine"), holder.coverImage
            );
        } else {
            CategoryColoringUtil.setupPlaceIcon(
                    context, data.get(position).getType(), holder.coverImage
            );
        }
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
        TextView address;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.placeTitle);
            coverImage = itemView.findViewById(R.id.placeImage);
            openingHours = itemView.findViewById(R.id.placeOpeningHours);
            address = itemView.findViewById(R.id.placeAddress);
        }
    }
}
