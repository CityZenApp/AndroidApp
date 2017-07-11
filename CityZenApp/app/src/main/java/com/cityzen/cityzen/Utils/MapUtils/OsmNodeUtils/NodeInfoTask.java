package com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils;

import android.os.AsyncTask;

import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Node;

/**
 * Created by Valdio Veliu on 03/05/2017.
 */

public class NodeInfoTask extends AsyncTask<Void, Void, Void> {

    private com.cityzen.cityzen.Utils.MapUtils.OsmNodeUtils.NodeResponseListener listener;
    private OsmConnection connection;
    private long nodeID;

    private Node node = null;

    public NodeInfoTask(OsmConnection connection, long nodeID, NodeResponseListener listener) {
        this.connection = connection;
        this.nodeID = nodeID;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        node = getNode();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (node != null)
            listener.onPoiReceived(node);
        else
            listener.onFailure();
    }

    private Node getNode() {
        MapDataDao mapDao = new MapDataDao(connection);
        return mapDao.getNode(nodeID);
    }

}
