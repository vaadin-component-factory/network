package com.vaadin.componentfactory.demo.data;

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.model.AbstractNetworkComponent;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;

public class CustomNetworkComponent extends AbstractNetworkComponent<CustomNetworkNode,CustomNetworkEdge> {

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
           setNodes(NetworkConverter.convertJsonToNetworkNodeList(value.getArray("nodes"), CustomNetworkNode.class));
           setEdges(NetworkConverter.convertJsonToNetworkEdgeList(value.getArray("edges"), CustomNetworkEdge.class));
        //
        return this;
    }
}
