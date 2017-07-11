package com.cityzen.cityzen.Utils.MapUtils.RouteUtils;

import org.osmdroid.views.overlay.Polyline;


/**
 * Created by Valdio Veliu on 06/05/2017.
 */

public interface PolylineCallback {
    void onPolylineCreated(Polyline polyline);

    void onFailure();
}
