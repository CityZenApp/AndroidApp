package com.cityzen.cityzen.Network;


/**
 * Created by Valdio Veliu on 11/05/2017.
 */

public interface ChangesetCallback {
    void onChangesetCreated(String changesetId);

    void onFailure(String errorMessage);
}
