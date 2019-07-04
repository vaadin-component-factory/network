package com.vaadin.componentfactory.converter;

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

import elemental.json.Json;
import elemental.json.JsonArray;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonArray;

import java.util.ArrayList;
import java.util.List;

public class NetworkConverter {

    public static JsonArray convertNetworkNodeListToJsonArray(List<NetworkNode> networkNodes) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < networkNodes.size(); i++) {
            array.set(i, networkNodes.get(i).toJson());
        }
        return array;
    }
    public static JsonArray convertNetworkEdgeListToJsonArray(List<NetworkEdge> networkEdges) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < networkEdges.size(); i++) {
            array.set(i, networkEdges.get(i).toJson());
        }
        return array;
    }
    public static JsonArray convertNetworkNodeListToJsonArrayOfIds(List<NetworkNode> networkNodes) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < networkNodes.size(); i++) {
            array.set(i, networkNodes.get(i).getId());
        }
        return array;
    }
    public static JsonArray convertNetworkEdgeListToJsonArrayOfIds(List<NetworkEdge> networkEdges) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < networkEdges.size(); i++) {
            array.set(i, networkEdges.get(i).getId());
        }
        return array;
    }

    public static List<NetworkNode> convertJsonToNetworkNodeList(JsonValue value) {
        List<NetworkNode> networkNodes = new ArrayList<>();
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)  value;
            for (int i = 0; i < array.length(); i++) {
                NetworkNode node = new NetworkNode();
                node.readJson(array.getObject(i));
                networkNodes.add(node);
            }
        } else if (value instanceof JsonObject){
            JsonObject jsonObject = (JsonObject)  value;
            if (jsonObject.hasKey("x")){
                // its an item
                NetworkNode node = new NetworkNode();
                node.readJson(jsonObject);
                networkNodes.add(node);
            } else {
                // its a map of nodes
                for (String id : jsonObject.keys()) {
                    JsonObject jsonNode = jsonObject.getObject(id);
                    NetworkNode node = new NetworkNode();
                    node.readJson(jsonNode);
                    networkNodes.add(node);
                }
            }
        }
        return networkNodes;
    }

    public static List<NetworkEdge> convertJsonToNetworkEdgeList(JsonValue value) {
        List<NetworkEdge> networkEdges = new ArrayList<>();
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)  value;
            for (int i = 0; i < array.length(); i++) {
                NetworkEdge edge = new NetworkEdge();
                edge.readJson(array.getObject(i));
                networkEdges.add(edge);
            }

        } else if (value instanceof JsonObject){
            JsonObject jsonObject = (JsonObject)  value;
            if (jsonObject.hasKey("from")){
                // its an item
                NetworkEdge edge = new NetworkEdge();
                edge.readJson(jsonObject);
                networkEdges.add(edge);
            } else {
                // its a map of edges
                for (String id : jsonObject.keys()) {
                    JsonObject jsonEdge = jsonObject.getObject(id);
                    NetworkEdge edge = new NetworkEdge();
                    edge.readJson(jsonEdge);
                    networkEdges.add(edge);
                }
            }
        }
        return networkEdges;

    }


    public static List<String> convertJsonToIdList(JsonValue jsonValue) {
        List<String> ids = new ArrayList<>();
        if (jsonValue instanceof  JsonArray) {
            JsonArray array = (JsonArray) jsonValue;
            for (int i = 0; i < array.length(); i++) {
                ids.add(array.getString(i));
            }
        } else {
            ids.add(jsonValue.asString());
        }
        return ids;
    }

    public static JsonValue convertIdListToJson(List<String> ids) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < ids.size(); i++) {
            array.set(i, ids.get(i));
        }
        return array;
    }
}
