package com.cityzen.cityzen.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.RecyclerView.CategoryDisplayConfig;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Valdio Veliu on 25/04/2017.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final int height;
    private final int width;
    private Context context;
    private TypedArray categories;

    public CategoryAdapter(Context context, Display display, TypedArray categories) {
        this.context = context;

        //get POI data from resources
        Resources res = context.getResources();
        this.categories = categories;
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        CategoryDisplayConfig categoryDisplayConfig = CategoryDisplayConfig.getById(categories.getInt(position,0));

        holder.container.setBackgroundColor(
                context.getResources().getColor(categoryDisplayConfig.color));
        holder.title.setText(categoryDisplayConfig.title);
        holder.imageView.setImageResource(categoryDisplayConfig.icon);

        //set the height manually to RecyclerView items
        holder.container.getLayoutParams().height = width / 3;
        holder.itemView.setTag(categoryDisplayConfig.id);
    }

    @Override
    public int getItemCount() {
        return categories.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView title;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.poi_container);
            title = itemView.findViewById(R.id.poi_title);
            imageView = itemView.findViewById(R.id.poi_image);
        }
    }
}
