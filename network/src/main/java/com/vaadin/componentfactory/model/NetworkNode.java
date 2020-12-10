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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Node displayed on the network graph
 * id should be unique in the entire network and not null
 * nodes and edges should not be null
 */
public interface NetworkNode<TNode extends NetworkNode, TEdge> extends JsonSerializable {

    public enum ComponentColor{
        RED(0),
        ORANGE(1),
        YELLOW(2),
        GREEN(3),
        DARK_GREEN(4),
        BLUE(5),
        PURPLE(6),
        VIOLET(7),
        BROWN(8);

        private final int value;

        private static Map<Integer,ComponentColor> map = new HashMap<>();

        ComponentColor(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }

        static {
            for (ComponentColor componentColor : ComponentColor.values()) {
                map.put(componentColor.value, componentColor);
            }
        }

        public static ComponentColor valueOf(int componentColor) {
            return map.get(componentColor);
        }
    }


    public enum NodeType{
        INPUT_TYPE("input"),
        OUTPUT_TYPE("output"),
        COMPONENT_TYPE("component");

        private static Map<String,NodeType> map = new HashMap<>();
        private final String value;

        NodeType(final String newValue) {
            value = newValue;
        }

        public String getValue() { return value; }
        static {
            for (NodeType nodeType : NodeType.values()) {
                map.put(nodeType.value, nodeType);
            }
        }

        public static NodeType fromString(String nodeType) {
            if (!map.containsKey(nodeType)){
                return null;
            }
            return map.get(nodeType);
        }
    }

    String getId();

    void setId(String id);

    String getLabel();

    void setLabel(String label);
    double getX();

    void setX(double x);

    double getY();

    void setY(double y);

    NodeType getType();

    void setType(NodeType type);

    ComponentColor getComponentColor();

    void setComponentColor(ComponentColor type);

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
            obj.put("type", getType().getValue());
        }
        if (getComponentColor() != null) {
            obj.put("componentColor", getComponentColor().getValue());
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
            setType(NodeType.fromString(value.getString("type")));
        }
        if (value.hasKey("componentColor")) {
            setComponentColor(ComponentColor.valueOf((int) value.getNumber("componentColor")));
        }
        return this;
    }

    /**
     * retrieve the node recursively inside the children
     *
     * @param id id of the node
     * @return node tih the id
     */
    default TNode findNodeById(String id) {
        if (getNodes().containsKey(id)){
            return getNodes().get(id);
        } else {
            // check in the child
            for (TNode childNode : getNodes().values()) {
                TNode foundNode = (TNode) childNode.findNodeById(id);
                if (foundNode != null){
                    return foundNode;
                }
            }
        }
        return null;
    }
}
