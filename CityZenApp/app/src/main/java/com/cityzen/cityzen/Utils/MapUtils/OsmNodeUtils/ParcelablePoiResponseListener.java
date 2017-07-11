package com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils;

import com.cityzen.cityzen.Models.ParcelablePOI;

/**
 * Created by Valdio Veliu on 03/05/2017.
 */

public interface ParcelablePoiResponseListener {
    void onPoiReceived(ParcelablePOI poi);

    void onFailure();
}
