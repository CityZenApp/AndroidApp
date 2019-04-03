package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.OpeningHours.OpeningHoursUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmTags;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import info.metadude.java.library.overpass.models.Element;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */
public class ElementListAdapter extends RecyclerView.Adapter<ElementListAdapter.ViewHolder> {

    private Context context;
    private String poiName = "";
    private int categoryId;
    private List<Element> data;// this is a temp file fof preview purposes

    public ElementListAdapter(Context context, String poiName, int categoryId, List<Element> data) {
        this.context = context;
        this.poiName = poiName;
        this.categoryId = categoryId;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poi_list_item, parent, false);
        return new ElementListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ElementListAdapter.ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(poiName);
        boolean hasName = false;
        boolean hasOpeningHours = false;
        String poiCategory = "";
        Map<String, String> tags = data.get(position).tags;
        if (tags != null)
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                if (tag.getKey().equals("name")) {
                    holder.title.setText(tag.getValue());
                    hasName = true;
                }

                if (tags.containsKey(OsmTags.OPENING_HOURS)) {
                    if (OpeningHoursUtils.isOpenNow(tags.get(OsmTags.OPENING_HOURS))) {
                        holder.openingHours.setText(R.string.open);
                        holder.openingHours.setTextColor(context.getResources().getColor(R.color.open));
                    } else {
                        holder.openingHours.setText(R.string.closed);
                        holder.openingHours.setTextColor(context.getResources().getColor(R.color.closed));
                    }
                    hasOpeningHours = true;
                }

                if (tag.getKey().equals("amenity") ||
                        tag.getKey().equals("shop") ||
                        tag.getKey().equals("tourism") ||
                        tag.getKey().equals("historic") ||
                        tag.getKey().equals("building") ||
                        tag.getKey().equals("public_transport") ||
                        tag.getKey().equals("healthcare")) {
                    poiCategory = tag.getValue();
                }

            }
        if (!hasName && !poiCategory.equals("") && poiCategory.length() > 1)
            holder.title.setText((poiCategory.substring(0, 1).toUpperCase() + poiCategory.substring(1).toLowerCase())
                    .replaceAll("_", " "));

        if (!hasName && tags != null)
            updatePoiNameIfNeeded(categoryId, tags, holder);

        if (hasOpeningHours)
            holder.openingHours.setVisibility(View.VISIBLE);
        else
            holder.openingHours.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void resetAdapter(List<Element> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView openingHours;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.poiTitle);
            openingHours = itemView.findViewById(R.id.poiOpeningHours);
        }
    }


    private void updatePoiNameIfNeeded(int categoryId, Map<String, String> tags, ViewHolder holder) {
        switch (categoryId) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:

                break;
            case 6:
                //Transportation
                if (tags != null)
                    for (Map.Entry<String, String> tag : tags.entrySet())
                        if (tag.getKey().equals("amenity") && tag.getValue() != null && tag.getValue().length() > 1) {
                            String transportation = tag.getValue().substring(0, 1).toUpperCase() + tag.getValue().substring(1);
                            holder.title.setText(transportation.replaceAll("_", " "));
                        }
                break;
            case 7:

                break;
            case 8:

                break;
            case 9:
                //atm
                if (tags != null)
                    for (Map.Entry<String, String> tag : tags.entrySet())
                        if (tag.getKey().equals("operator"))
                            holder.title.setText(tag.getValue());

                break;
            case 10:

                break;
            case 11:

                break;
            case 12:

                break;
            case 13:

                break;
            case 14:

                break;
            case 15:

                break;
            default:
                break;
        }
    }
}
