package com.cityzen.cityzen.Utils.Development;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public class AppToast {
    Context context;

    public AppToast(Context context) {
        this.context = context;
    }

    public void toast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public void centerViewToast(String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public void toast(int s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public void longToast(int s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
