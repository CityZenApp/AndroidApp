package com.cityzen.cityzen.Utils.MapUtils.PoiCategoryFilter;

import java.util.List;

import info.metadude.java.library.overpass.models.Element;

/**
 * Created by Valdio Veliu on 29/04/2017.
 */

public interface PoiResponseListener {
    void onPoiReceived(List<Element> elements);

    void onFailure();
}
