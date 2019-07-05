package com.vaadin.componentfactory.demo.data;

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
import com.vaadin.componentfactory.model.NetworkNode;
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
public class NetworkNodeImpl implements NetworkNode<NetworkNodeImpl,NetworkEdgeImpl> {

    private UUID id;
    private String label;
    private double x;
    private double y;
    private String type;


    public NetworkNodeImpl() {

    }

    public String getId() {
        if (id == null){
            id = UUID.randomUUID();
        }
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNodeImpl that = (NetworkNodeImpl) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return label;
    }

    private List<NetworkNodeImpl> nodes = new ArrayList<>();

    private List<NetworkEdgeImpl> edges = new ArrayList<>();

    public List<NetworkNodeImpl> getNodes() {
        return nodes;
    }

    public void setNodes(List<NetworkNodeImpl> nodes) {
        this.nodes = nodes;
    }

    public List<NetworkEdgeImpl> getEdges() {
        return edges;
    }

    public void setEdges(List<NetworkEdgeImpl> edges) {
        this.edges = edges;
    }
    @Override
    public JsonObject toJson() {
        JsonObject obj = Json.createObject();
        obj.put("id", getId());
        if (getLabel() != null) {
            obj.put("label", getLabel());
        }
        obj.put("x",getX());
        obj.put("y",getY());
        if (getType() != null) {
            obj.put("type", getType());
        }
        // put nodes and edges
        obj.put("nodes", NetworkConverter.convertNetworkNodeListToJsonArray(getNodes()));
        obj.put("edges", NetworkConverter.convertNetworkEdgeListToJsonArray(getEdges()));
        return obj;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        if (value.hasKey("id")) {
            setId(value.getString("id"));
        }
        setLabel(value.getString("label"));
        setX(value.getNumber("x"));
        setY(value.getNumber("y"));
        if (value.hasKey("type")) {
            setType(value.getString("type"));
        }
        setNodes(NetworkConverter.convertJsonToObjectList(value.getArray("nodes"), NetworkNodeImpl.class));
        setEdges(NetworkConverter.convertJsonToObjectList(value.getArray("edges"), NetworkEdgeImpl.class));
        return this;
    }
}
