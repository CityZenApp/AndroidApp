package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cityzen.cityzen.Models.OsmFeature;
import com.cityzen.cityzen.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */
public class PoiFeaturesAdapter extends RecyclerView.Adapter<PoiFeaturesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OsmFeature> data;// this is a temp file fof preview purposes

    public PoiFeaturesAdapter(Context context, ArrayList<OsmFeature> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.features_item, parent, false);
        PoiFeaturesAdapter.ViewHolder holder = new PoiFeaturesAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(PoiFeaturesAdapter.ViewHolder holder, int position) {
        holder.featureTextView.setText(data.get(position).getKey() + " " + data.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void resetAdapter(ArrayList<OsmFeature> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public void resetAdapter() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView featureTextView;

        ViewHolder(View itemView) {
            super(itemView);
            featureTextView = (TextView) itemView.findViewById(R.id.featureTextView);
        }
    }
}
