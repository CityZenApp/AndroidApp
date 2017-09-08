package com.cityzen.cityzen.Utils.Development;

import android.util.Log;

/**
 * Created by Valdio Veliu on 26/04/2017.
 */

public class AppLog {
    public static void log(String s) {
        Log.wtf("CITYZEN", s);
    }

    public static void log(int s) {
        Log.wtf("CITYZEN", String.valueOf(s));
    }

    public static void log(Object s) {
        Log.wtf("CITYZEN", String.valueOf(s));
    }
}
