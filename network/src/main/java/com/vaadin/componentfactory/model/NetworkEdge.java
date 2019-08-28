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

import java.util.UUID;

/**
 * Interface for edge between 2 nodes.
 * from, to and id are required
 * id should be unique in the entire network and not null
 */
public interface NetworkEdge extends JsonSerializable {

    String FROM_CLIENT_PROPERTY = "modelFrom";
    String TO_CLIENT_PROPERTY = "modelTo";

    String getId();

    void setId(String id);

    /**
     *
     * @return id of the source node
     */
    String getFrom();
    /**
     * set the id of the source node
     *
     * @param from id
     */
    void setFrom(String from);

    /**
     *
     * @return id of the destination node
     */
    String getTo();
    /**
     * set the id of the destination node
     *
     * @param to id
     */
    void setTo(String to);


    /**
     *
     * @return json object
     */
    default JsonObject toJson() {
        JsonObject obj = Json.createObject();
        if (getId() != null) {
            obj.put("id", getId());
        }
        obj.put(FROM_CLIENT_PROPERTY,getFrom());
        obj.put(TO_CLIENT_PROPERTY,getTo());
        return obj;
    }

    default JsonSerializable readJson(JsonObject value) {
        if (value.hasKey("id")) {
            setId(value.getString("id"));
        }
        setFrom(value.getString(FROM_CLIENT_PROPERTY));
        setTo(value.getString(TO_CLIENT_PROPERTY));
        return this;
    }

}
