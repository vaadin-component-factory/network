package com.vaadin.componentfactory.model;

/*
 * #%L
 * Network Component
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.*;

/**
 * Node displayed on the network graph
 * The simplest implementation of a Network Node
 */
public class NetworkNodeImpl implements NetworkNode<NetworkNodeImpl,NetworkEdgeImpl> {

    private UUID id;
    private String label;
    private double x;
    private double y;
    private NodeType type;
    private ComponentColor componentColor;


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

    public NodeType getType() {
        return type;
    }

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

    private Map<String,NetworkNodeImpl> nodes = new HashMap<>();

    private Map<String,NetworkEdgeImpl> edges = new HashMap<>();

    @Override
    public Map<String, NetworkNodeImpl> getNodes() {
        return nodes;
    }

    @Override
    public void setNodes(Map<String, NetworkNodeImpl> nodes) {
        this.nodes = nodes;
    }

    @Override
    public Map<String, NetworkEdgeImpl> getEdges() {
        return edges;
    }

    @Override
    public void setEdges(Map<String, NetworkEdgeImpl> edges) {
        this.edges = edges;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = NetworkNode.super.toJson();
        // put nodes and edges
        obj.put("nodes", NetworkConverter.convertNetworkNodeListToJsonArray(getNodes().values()));
        obj.put("edges", NetworkConverter.convertNetworkEdgeListToJsonArray(getEdges().values()));
        return obj;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        NetworkNode.super.readJson(value);
        if (value.hasKey("nodes")) {
            setNodes(NetworkConverter.convertJsonToNodeMap(value.getArray("nodes"), NetworkNodeImpl.class));
        }
        if (value.hasKey("edges")) {
            setEdges(NetworkConverter.convertJsonToEdgeMap(value.getArray("edges"), NetworkEdgeImpl.class));
        }
        return this;
    }
}
