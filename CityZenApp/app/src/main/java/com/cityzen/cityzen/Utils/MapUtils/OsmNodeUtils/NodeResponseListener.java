package com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils;

import de.westnordost.osmapi.map.data.Node;

/**
 * Created by Valdio Veliu on 03/05/2017.
 */

public interface NodeResponseListener {
    void onPoiReceived(Node node);

    void onFailure();
}
