package com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser;

import java.util.ArrayList;

/**
 * Created by Valdio Veliu on 8/19/15.
 */
public interface Action {
    public void action(ArrayList<Place> places, String error);
}
