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

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonArray;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Network Converter to convert TNode/TEdge from/to JsonValue
 *
 * @param <TNode> Node type - Implementation of NetworkNode must have a constructor with no parameters
 * @param <TEdge> Edge type - Implementation of NetworkEdge must have a constructor with no parameters
 *
 * @author Vaadin Ltd
 */
public class NetworkConverter<TNode extends NetworkNode<TNode,TEdge>,TEdge extends NetworkEdge> {
    private final Class<TNode> nodeClass;
    private final Class<TEdge> edgeClass;

    public NetworkConverter(Class<TNode> nodeClass, Class<TEdge> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
    }

    /**
     * Convert node list to json array
     *
     * @param networkNodes list of nodes
     * @return Jsonarray of nodes
     */
    public static JsonArray convertNetworkNodeListToJsonArray(Collection<? extends JsonSerializable> networkNodes) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (JsonSerializable networkNode : networkNodes) {
            array.set(i, networkNode.toJson());
            i++;
        }
        return array;
    }

    /**
     * Convert edge list to json array
     *
     * @param networkEdges list of edges
     * @return Jsonarray of edges
     */
    public static JsonArray convertNetworkEdgeListToJsonArray(Collection<? extends JsonSerializable> networkEdges) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (JsonSerializable networkEdge : networkEdges) {
            array.set(i,networkEdge.toJson());
            i++;
        }
        return array;
    }

    /**
     * Convert node list to a jsonarray of ids
     *
     * @param networkNodes list of network nodes
     * @return Jsonarray of ids
     */
    public JsonArray convertNetworkNodeListToJsonArrayOfIds(Collection<TNode> networkNodes) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (TNode networkNode : networkNodes) {
            array.set(i,networkNode.getId());
            i++;
        }
        return array;
    }

    /**
     *
     * Convert edge list to a jsonarray of ids
     *
     * @param networkEdges list of network edges
     * @return Jsonarray of ids
     */
    public JsonArray convertNetworkEdgeListToJsonArrayOfIds(Collection<TEdge> networkEdges) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (TEdge networkEdge : networkEdges) {
            array.set(i,networkEdge.getId());
            i++;
        }
        return array;
    }

    /**
     * Convert the json value from the client side to a list of nodes
     *
     *
     * @param value jsonvalue representing an object node, a map of nodes or a list of nodes
     * @return list of nodes
     */
    public List<TNode> convertJsonToNodeList(JsonValue value) {
        return convertJsonToObjectList(value, nodeClass);
    }

    public static <T extends JsonSerializable> List<T> convertJsonToObjectList(JsonValue value, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        try {
            if (value instanceof JsonArray) {
                JsonArray array = (JsonArray)  value;
                for (int i = 0; i < array.length(); i++) {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(array.getObject(i));
                    list.add(node);
                }
            } else if (value instanceof JsonObject){
                JsonObject jsonObject = (JsonObject)  value;
                if (jsonObject.hasKey("id")|| jsonObject.hasKey("x") || jsonObject.hasKey(NetworkEdge.FROM_CLIENT_PROPERTY)){
                    // its an item
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(jsonObject);
                    list.add(node);
                } else {
                    // its a map of nodes
                    for (String id : jsonObject.keys()) {
                        JsonObject jsonNode = jsonObject.getObject(id);
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(jsonNode);
                        list.add(node);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate new bean", e);
        }
        return list;
    }

    public List<TEdge> convertJsonToNetworkEdgeList(JsonValue value) {
        return convertJsonToObjectList(value, edgeClass);
    }

    /**
     *
     * Convert a jsnvalue to a map of nodes
     *
     * @param value  jsonvalue representing an object node, a map of nodes or a list of nodes
     * @param tClass class of the implementation of NetworkNode
     * @param <T> Implementation of NetworkNode must have a constructor with no parameters
     * @return Map of nodes key = node.id
     */
    public static <T extends NetworkNode> Map<String,T> convertJsonToNodeMap(JsonValue value, Class<T> tClass) {
        HashMap<String, T> map = new HashMap<>();
        try {
            if (value instanceof JsonArray) {
                JsonArray array = (JsonArray)  value;
                for (int i = 0; i < array.length(); i++) {
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(array.getObject(i));
                        map.put(node.getId(),node);
                }
            } else if (value instanceof JsonObject){
                JsonObject jsonObject = (JsonObject)  value;
                if (jsonObject.hasKey("x")){
                    // its an item
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(jsonObject);
                    map.put(node.getId(),node);
                } else {
                    // its a map of nodes
                    for (String id : jsonObject.keys()) {
                        JsonObject jsonNode = jsonObject.getObject(id);
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(jsonNode);
                        map.put(node.getId(),node);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate new bean", e);
        }
        return map;
    }

    /**
     *
     * Convert a jsnvalue to a map of edges
     *
     * @param value  jsonvalue representing an object edge, a map of edges or a list of edges
     * @param tClass class of the implementation of NetworkEdge
     * @param <T> Implementation of NetworkEdge must have a constructor with no parameters
     * @return Map of edges key = edge.id
     */
    public static <T extends NetworkEdge> Map<String,T> convertJsonToEdgeMap(JsonValue value, Class<T> tClass) {
        HashMap<String, T> map = new HashMap<>();
        try {
            if (value instanceof JsonArray) {
                JsonArray array = (JsonArray)  value;
                for (int i = 0; i < array.length(); i++) {
                    T edge = tClass.getDeclaredConstructor().newInstance();
                    edge.readJson(array.getObject(i));
                    map.put(edge.getId(),edge);
                }
            } else if (value instanceof JsonObject){
                JsonObject jsonObject = (JsonObject)  value;
                if (jsonObject.hasKey(NetworkEdge.FROM_CLIENT_PROPERTY)){
                    // its an item
                    T edge = tClass.getDeclaredConstructor().newInstance();
                    edge.readJson(jsonObject);
                    map.put(edge.getId(),edge);
                } else {
                    // its a map of edges
                    for (String id : jsonObject.keys()) {
                        JsonObject jsonEdge = jsonObject.getObject(id);
                        T edge = tClass.getDeclaredConstructor().newInstance();
                        edge.readJson(jsonEdge);
                        map.put(edge.getId(),edge);

                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate new bean", e);
        }
        return map;
    }


    /**
     * Convert a jsonValue to a list of ids
     *
     * @param jsonValue jsonarray of id or a single id
     * @return list of ids
     */
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

    /**
     * Convert list of ids to json array
     *
     * @param ids list of ids
     * @return json array of ids
     */
    public static JsonValue convertIdListToJson(List<String> ids) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < ids.size(); i++) {
            array.set(i, ids.get(i));
        }
        return array;
    }
}
