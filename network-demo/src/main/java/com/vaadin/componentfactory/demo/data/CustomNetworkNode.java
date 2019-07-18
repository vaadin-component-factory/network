package com.vaadin.componentfactory.demo.data;

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.*;

public class CustomNetworkNode implements NetworkNode<CustomNetworkNode,CustomNetworkEdge> {

    private UUID id;
    private String label;
    private double x;
    private double y;
    private NodeType type;
    private ComponentColor componentColor;
    private String customField;

    private Map<String,CustomNetworkNode> nodes = new HashMap<>();

    private Map<String,CustomNetworkEdge> edges = new HashMap<>();

    public CustomNetworkNode() {

    }
    @Override
    public String getId() {
        if (id == null){
            id = UUID.randomUUID();
        }
        return id.toString();
    }
    @Override
    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public NodeType getType() {
        return type;
    }

    @Override
    public void setType(NodeType type) {
        this.type = type;
    }

    @Override
    public ComponentColor getComponentColor() {
        return componentColor;
    }

    @Override
    public void setComponentColor(ComponentColor componentColor) {
        this.componentColor = componentColor;
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

    @Override
    public Map<String, CustomNetworkNode> getNodes() {
        return nodes;
    }

    @Override
    public void setNodes(Map<String, CustomNetworkNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Map<String, CustomNetworkEdge> getEdges() {
        return edges;
    }

    @Override
    public void setEdges(Map<String, CustomNetworkEdge> edges) {
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
        JsonObject obj = NetworkNode.super.toJson();

        if (getCustomField() != null) {
            obj.put("custom-field", getCustomField());
        }
        // put nodes and edges
        obj.put("nodes", NetworkConverter.convertNetworkNodeListToJsonArray(getNodes().values()));
        obj.put("edges", NetworkConverter.convertNetworkEdgeListToJsonArray(getEdges().values()));
        return obj;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        NetworkNode.super.readJson(value);
        if (value.hasKey("custom-field")) {
            setCustomField(value.getString("custom-field"));
        }
        setNodes(NetworkConverter.convertJsonToNodeMap(value.getArray("nodes"), CustomNetworkNode.class));
        setEdges(NetworkConverter.convertJsonToEdgeMap(value.getArray("edges"), CustomNetworkEdge.class));
        return this;
    }

}
