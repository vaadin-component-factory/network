package com.vaadin.componentfactory.model;

/*
 * #%L
 * Network Component
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Node displayed on the network graph
 */
public class NetworkComponent extends AbstractNetworkComponent<NetworkNode,NetworkEdge> {

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        // put nodes and edges
        jsonObject.put("nodes", NetworkConverter.convertNetworkNodeListToJsonArray(getNodes()));
        jsonObject.put("edges", NetworkConverter.convertNetworkEdgeListToJsonArray(getEdges()));
        return jsonObject;
    }


    @Override
    public JsonSerializable readJson(JsonObject value) {
        super.readJson(value);
        setNodes(NetworkConverter.convertJsonToNetworkNodeList(value.getArray("nodes"), NetworkNode.class));
        setEdges(NetworkConverter.convertJsonToNetworkEdgeList(value.getArray("edges"), NetworkEdge.class));
        //
        return this;
    }
}
