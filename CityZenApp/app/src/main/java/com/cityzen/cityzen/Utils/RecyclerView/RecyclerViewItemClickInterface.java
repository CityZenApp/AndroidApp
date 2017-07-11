package com.cityzen.cityzen.Utils.RecyclerView;

import android.view.View;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */

public interface RecyclerViewItemClickInterface {
    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}