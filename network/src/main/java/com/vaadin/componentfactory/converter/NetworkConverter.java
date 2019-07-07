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

public class NetworkConverter<TNode extends NetworkNode<TNode,TEdge>,TEdge extends NetworkEdge> {
    private final Class<TNode> nodeClass;
    private final Class<TEdge> edgeClass;

    public NetworkConverter(Class<TNode> nodeClass, Class<TEdge> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
    }

    public static JsonArray convertNetworkNodeListToJsonArray(Collection<? extends JsonSerializable> networkNodes) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (JsonSerializable networkNode : networkNodes) {
            array.set(i, networkNode.toJson());
            i++;
        }
        return array;
    }
    public static JsonArray convertNetworkEdgeListToJsonArray(Collection<? extends JsonSerializable> networkEdges) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (JsonSerializable networkEdge : networkEdges) {
            array.set(i,networkEdge.toJson());
            i++;
        }
        return array;
    }
    public JsonArray convertNetworkNodeListToJsonArrayOfIds(Collection<TNode> networkNodes) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (TNode networkNode : networkNodes) {
            array.set(i,networkNode.getId());
            i++;
        }
        return array;
    }
    public JsonArray convertNetworkEdgeListToJsonArrayOfIds(Collection<TEdge> networkEdges) {
        JsonArray array = Json.createArray();
        int i = 0;
        for (TEdge networkEdge : networkEdges) {
            array.set(i,networkEdge.getId());
            i++;
        }
        return array;
    }

    public List<TNode> convertJsonToObjectList(JsonValue value) {
        return convertJsonToObjectList(value, nodeClass);
    }

    public static <T extends JsonSerializable> List<T> convertJsonToObjectList(JsonValue value, Class<T> tClass) {
        List<T> list = new ArrayList<>();
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)  value;
            for (int i = 0; i < array.length(); i++) {
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(array.getObject(i));
                    list.add(node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else if (value instanceof JsonObject){
            JsonObject jsonObject = (JsonObject)  value;
            if (jsonObject.hasKey("x")|| jsonObject.hasKey("from")){
                // its an item
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(jsonObject);
                    list.add(node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                // its a map of nodes
                for (String id : jsonObject.keys()) {
                    JsonObject jsonNode = jsonObject.getObject(id);
                    try {
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(jsonNode);
                        list.add(node);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    public List<TEdge> convertJsonToNetworkEdgeList(JsonValue value) {
        return convertJsonToObjectList(value, edgeClass);
    }

    public static <T extends NetworkNode> Map<String,T> convertJsonToNodeMap(JsonValue value, Class<T> tClass) {
        HashMap<String, T> map = new HashMap<>();
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)  value;
            for (int i = 0; i < array.length(); i++) {
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(array.getObject(i));
                    map.put(node.getId(),node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else if (value instanceof JsonObject){
            JsonObject jsonObject = (JsonObject)  value;
            if (jsonObject.hasKey("x")){
                // its an item
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(jsonObject);
                    map.put(node.getId(),node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                // its a map of nodes
                for (String id : jsonObject.keys()) {
                    JsonObject jsonNode = jsonObject.getObject(id);
                    try {
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(jsonNode);
                        map.put(node.getId(),node);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }



    public static <T extends NetworkEdge> Map<String,T> convertJsonToEdgeMap(JsonValue value, Class<T> tClass) {
        HashMap<String, T> map = new HashMap<>();
        if (value instanceof JsonArray) {
            JsonArray array = (JsonArray)  value;
            for (int i = 0; i < array.length(); i++) {
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(array.getObject(i));
                    map.put(node.getId(),node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } else if (value instanceof JsonObject){
            JsonObject jsonObject = (JsonObject)  value;
            if (jsonObject.hasKey("from")){
                // its an item
                try {
                    T node = tClass.getDeclaredConstructor().newInstance();
                    node.readJson(jsonObject);
                    map.put(node.getId(),node);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                // its a map of nodes
                for (String id : jsonObject.keys()) {
                    JsonObject jsonNode = jsonObject.getObject(id);
                    try {
                        T node = tClass.getDeclaredConstructor().newInstance();
                        node.readJson(jsonNode);
                        map.put(node.getId(),node);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
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
