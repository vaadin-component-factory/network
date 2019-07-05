package com.vaadin.componentfactory.demo.data;

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CustomNetworkNode implements NetworkNode<CustomNetworkNode,CustomNetworkEdge> {

    private UUID id;
    private String label;
    private double x;
    private double y;
    private String type;
    private String customField;

    public CustomNetworkNode() {

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
        CustomNetworkNode that = (CustomNetworkNode) o;
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

    private List<CustomNetworkNode> nodes = new ArrayList<>();

    private List<CustomNetworkEdge> edges = new ArrayList<>();

    public List<CustomNetworkNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<CustomNetworkNode> nodes) {
        this.nodes = nodes;
    }

    public List<CustomNetworkEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<CustomNetworkEdge> edges) {
        this.edges = edges;
    }

    public String getCustomField() {
        return customField;
    }

    public void setCustomField(String customField) {
        this.customField = customField;
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
        if (getCustomField() != null) {
            obj.put("custom-field", getCustomField());
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
        if (value.hasKey("custom-field")) {
            setCustomField(value.getString("custom-field"));
        }
        setNodes(NetworkConverter.convertJsonToObjectList(value.getArray("nodes"), CustomNetworkNode.class));
        setEdges(NetworkConverter.convertJsonToObjectList(value.getArray("edges"), CustomNetworkEdge.class));
        return this;
    }

}
