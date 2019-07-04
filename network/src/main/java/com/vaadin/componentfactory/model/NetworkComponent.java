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

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Node displayed on the network graph
 */
public class NetworkComponent extends NetworkNode {

    private List<NetworkNode> nodes = new ArrayList<>();

    private List<NetworkEdge> edges = new ArrayList<>();

    public List<NetworkNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<NetworkNode> nodes) {
        this.nodes = nodes;
    }

    public List<NetworkEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<NetworkEdge> edges) {
        this.edges = edges;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();

        // put nodes and edges
        jsonObject.put("nodes", NetworkConverter.convertNetworkNodeListToJsonArray(nodes));
        jsonObject.put("edges", NetworkConverter.convertNetworkEdgeListToJsonArray(edges));
        return jsonObject;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        super.readJson(value);
        setNodes(NetworkConverter.convertJsonToNetworkNodeList(value.getArray("nodes")));
        setEdges(NetworkConverter.convertJsonToNetworkEdgeList(value.getArray("edges")));
        //
        return this;
    }
}
