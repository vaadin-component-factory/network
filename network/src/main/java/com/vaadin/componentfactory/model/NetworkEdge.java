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
