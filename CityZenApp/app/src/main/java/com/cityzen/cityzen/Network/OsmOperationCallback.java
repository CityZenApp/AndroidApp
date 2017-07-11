package com.cityzen.cityzen.Network;


/**
 * Created by Valdio Veliu on 11/05/2017.
 */

public interface OsmOperationCallback {
    void osmOperationSuccessful(String response);

    void onFailure(String errorMessage);
}
