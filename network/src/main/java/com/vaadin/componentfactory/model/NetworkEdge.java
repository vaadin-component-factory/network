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
 * link between 2 nodes
 */
public interface NetworkEdge extends JsonSerializable {


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


    default JsonObject toJson() {
        JsonObject obj = Json.createObject();
        if (getId() != null) {
            obj.put("id", getId());
        }
        obj.put("from",getFrom());
        obj.put("to",getTo());
        return obj;
    }

    default JsonSerializable readJson(JsonObject value) {
        if (value.hasKey("id")) {
            setId(value.getString("id"));
        }
        setFrom(value.getString("from"));
        setTo(value.getString("to"));
        return this;
    }

}
