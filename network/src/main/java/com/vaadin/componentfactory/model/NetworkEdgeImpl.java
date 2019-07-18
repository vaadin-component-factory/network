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


import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.util.UUID;

/**
 * link between 2 nodes
 * The simplest implementation of a Network Edge
 */
public class NetworkEdgeImpl implements NetworkEdge {

    private UUID id;
    private String from;
    private String to;

    public String getId() {
        if (id == null){
            id = UUID.randomUUID();
        }
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    /**
     *
     * @return id of the source node
     */
    public String getFrom() {
        return from;
    }

    /**
     * set the id of the source node
     *
     * @param from id
     */
    public void setFrom(String from) {
        this.from = from;
    }


    /**
     *
     * @return id of the destination node
     */
    public String getTo() {
        return to;
    }

    /**
     * set the id of the destination node
     *
     * @param to id
     */
    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = NetworkEdge.super.toJson();
        return obj;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        NetworkEdge.super.readJson(value);
        return this;
    }

    @Override
    public String toString() {
        return from + " ==>" + to;
    }
}
