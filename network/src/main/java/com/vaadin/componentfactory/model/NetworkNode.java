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

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Node displayed on the network graph
 */
public interface NetworkNode<TNode extends NetworkNode, TEdge> extends JsonSerializable {


    String getId();

    void setId(String id);

    String getLabel();

    void setLabel(String label);
    double getX();

    void setX(double x);

    double getY();

    void setY(double y);

    String getType();

    void setType(String type);

    Map<String,TNode> getNodes();

    void setNodes(Map<String,TNode> nodes);

    Map<String,TEdge> getEdges();

    void setEdges(Map<String,TEdge> edges);

    default JsonObject toJson() {
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
        return obj;
    }

    default JsonSerializable readJson(JsonObject value) {
        if (value.hasKey("id")) {
            setId(value.getString("id"));
        }
        setLabel(value.getString("label"));
        setX(value.getNumber("x"));
        setY(value.getNumber("y"));
        if (value.hasKey("type")) {
            setType(value.getString("type"));
        }
        return this;
    }
}
