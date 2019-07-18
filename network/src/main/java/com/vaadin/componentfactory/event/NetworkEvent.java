package com.vaadin.componentfactory.event;

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

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import elemental.json.JsonValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Class for creating event types for Network
 *
 */
public class NetworkEvent {

    private static final String EVENT_PREVENT_DEFAULT_JS = "event.preventDefault()";

    @DomEvent("vcf-network-selection")
    public static class NetworkSelectionEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TNode> networkNodes;
        private List<TEdge> networkEdges;

        public NetworkSelectionEvent(Network<TNode, TEdge> source, boolean fromClient,
                                     @EventData("event.detail.nodes") JsonValue nodes,
                                     @EventData("event.detail.edges") JsonValue edges) {
            super(source, fromClient);
            List<String> nodeIds = NetworkConverter.convertJsonToIdList(nodes);
            networkNodes = new ArrayList<>();
            for (String nodeId : nodeIds) {
                if (source.getCurrentData().getNodes().containsKey(nodeId)) {
                    networkNodes.add(source.getCurrentData().getNodes().get(nodeId));
                }
            }
            List<String> edgeIds = NetworkConverter.convertJsonToIdList(edges);
            networkEdges = new ArrayList<>();
            for (String edgeId : edgeIds) {
                if (source.getCurrentData().getEdges().containsKey(edgeId)) {
                    networkEdges.add(source.getCurrentData().getEdges().get(edgeId));
                }
            }
        }

        public List<TNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<TNode> networkNodes) {
            this.networkNodes = networkNodes;
        }

        public List<TEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<TEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }

    @DomEvent("vcf-network-new-nodes")
    public static class NetworkNewNodesEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TNode> networkNodes;

        public NetworkNewNodesEvent(Network<TNode, ?> source, boolean fromClient,
                                       @EventData("event.detail.items") JsonValue items,
                                       @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);

            networkNodes = source.getNetworkConverter().convertJsonToNodeList(items);
        }

        public List<TNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<TNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }


    @DomEvent("vcf-network-new-edges")
    public static class NetworkNewEdgesEvent<TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TEdge> networkEdges;

        public NetworkNewEdgesEvent(Network<?,TEdge> source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdges = source.getNetworkConverter().convertJsonToNetworkEdgeList(items);
        }

        public List<TEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<TEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }


    @DomEvent("vcf-network-after-new-nodes")
    public static class NetworkAfterNewNodesEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TNode> networkNodes;

        public NetworkAfterNewNodesEvent(Network<TNode, ?> source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);

            networkNodes = source.getNetworkConverter().convertJsonToNodeList(items);
        }

        public NetworkAfterNewNodesEvent(Network<TNode, ?> source,
                                         List<TNode> networkNodes) {
            super(source, false);
            this.networkNodes = networkNodes;
        }
        public List<TNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<TNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }


    @DomEvent("vcf-network-after-new-edges")
    public static class NetworkAfterNewEdgesEvent<TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TEdge> networkEdges;

        public NetworkAfterNewEdgesEvent(Network<?,TEdge>  source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdges = source.getNetworkConverter().convertJsonToNetworkEdgeList(items);
        }

        public NetworkAfterNewEdgesEvent(Network source, List<TEdge> networkEdges) {
            super(source, false);
            this.networkEdges = networkEdges;
        }
        public List<TEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<TEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }


    @DomEvent("vcf-network-delete-nodes")
    public static class NetworkDeleteNodesEvent extends ComponentEvent<Network> {
        private List<String> networkNodesId;

        public NetworkDeleteNodesEvent(Network source, boolean fromClient,
                                       @EventData("event.detail.ids") JsonValue ids,
                                       @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkNodesId = NetworkConverter.convertJsonToIdList(ids);
        }

        public List<String> getNetworkNodesId() {
            return networkNodesId;
        }

        public void setNetworkNodesId(List<String> networkNodesId) {
            this.networkNodesId = networkNodesId;
        }
    }


    @DomEvent("vcf-network-delete-edges")
    public static class NetworkDeleteEdgesEvent extends ComponentEvent<Network> {
        private List<String> networkEdgesId;

        public NetworkDeleteEdgesEvent(Network source, boolean fromClient,
                                       @EventData("event.detail.ids") JsonValue ids,
                                       @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdgesId = NetworkConverter.convertJsonToIdList(ids);
        }

        public List<String> getNetworkEdgesId() {
            return networkEdgesId;
        }

        public void setNetworkEdgesId(List<String> networkEdgesId) {
            this.networkEdgesId = networkEdgesId;
        }
    }




    @DomEvent("vcf-network-after-delete-nodes")
    public static class NetworkAfterDeleteNodesEvent extends ComponentEvent<Network> {
        private List<String> networkNodesId;

        public NetworkAfterDeleteNodesEvent(Network source, boolean fromClient, @EventData("event.detail.ids") JsonValue ids) {
            super(source, fromClient);
            networkNodesId = NetworkConverter.convertJsonToIdList(ids);
        }

        public NetworkAfterDeleteNodesEvent(Network source, List<String> networkNodesId) {
            super(source, false);
            this.networkNodesId = networkNodesId;
        }
        public List<String> getNetworkNodesId() {
            return networkNodesId;
        }

        public void setNetworkNodesId(List<String> networkNodesId) {
            this.networkNodesId = networkNodesId;
        }
    }


    @DomEvent("vcf-network-after-delete-edges")
    public static class NetworkAfterDeleteEdgesEvent extends ComponentEvent<Network> {
        private List<String> networkEdgesId;

        public NetworkAfterDeleteEdgesEvent(Network source, boolean fromClient, @EventData("event.detail.ids") JsonValue ids) {
            super(source, fromClient);
            networkEdgesId = NetworkConverter.convertJsonToIdList(ids);
        }

        public NetworkAfterDeleteEdgesEvent(Network source, List<String> networkEdgesId) {
            super(source, false);
            this.networkEdgesId = networkEdgesId;
        }
        public List<String> getNetworkEdgesId() {
            return networkEdgesId;
        }

        public void setNetworkEdgesId(List<String> networkEdgesId) {
            this.networkEdgesId = networkEdgesId;
        }
    }

    @DomEvent("vcf-network-create-component")
    public static class NetworkNewComponentEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TNode> networkNodes;

        public NetworkNewComponentEvent(Network<TNode,TEdge> source, boolean fromClient,
                                        @EventData("event.detail.nodes") JsonValue nodes,
                                        @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            List<String> nodeIds = NetworkConverter.convertJsonToIdList(nodes);
            networkNodes = new ArrayList<>();
            for (String nodeId : nodeIds) {
                if (source.getCurrentData().getNodes().containsKey(nodeId)) {
                    networkNodes.add(source.getCurrentData().getNodes().get(nodeId));
                }
            }
        }

        public List<TNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<TNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }

    @DomEvent("vcf-network-update-nodes")
    public static class NetworkUpdateNodesEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TNode> networkNodes;

        public NetworkUpdateNodesEvent(Network<TNode, ?> source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);

            networkNodes = source.getNetworkConverter().convertJsonToNodeList(items);
        }

        public List<TNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<TNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }


    @DomEvent("vcf-network-update-edges")
    public static class NetworkUpdateEdgesEvent<TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private List<TEdge> networkEdges;

        public NetworkUpdateEdgesEvent(Network<?,TEdge> source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdges = source.getNetworkConverter().convertJsonToNetworkEdgeList(items);
        }

        public List<TEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<TEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }


    @DomEvent("vcf-network-update-coordinates")
    public static class NetworkUpdateCoordinatesEvent extends ComponentEvent<Network> {
        private String nodeId;
        private double x;
        private  double y;

        public NetworkUpdateCoordinatesEvent(Network source, boolean fromClient,
                                       @EventData("event.detail.id") String nodeId,
                                       @EventData("event.detail.x") double x,
                                       @EventData("event.detail.y") double y,
                                       @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            this.nodeId = nodeId;
            this.x = x;
            this.y = y;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
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
    }



    @DomEvent("vcf-network-hover-node")
    public static class NetworkHoverNodeEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private TNode node;

        public NetworkHoverNodeEvent(Network<TNode, ?> source, boolean fromClient,
                                             @EventData("event.detail.id") String nodeId) {
            super(source, fromClient);

            if (source.getCurrentData().getNodes().containsKey(nodeId)){
                node = source.getCurrentData().getNodes().get(nodeId);
            }
        }

        public TNode getNode() {
            return node;
        }

        public void setNode(TNode node) {
            this.node = node;
        }
    }



    @DomEvent("vcf-network-hover-edge")
    public static class NetworkHoverEdgeEvent<TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private TEdge edge;

        public NetworkHoverEdgeEvent(Network<?, TEdge> source, boolean fromClient,
                                     @EventData("event.detail.id") String edgeId) {
            super(source, fromClient);
            if (source.getCurrentData().getEdges().containsKey(edgeId)){
                edge = source.getCurrentData().getEdges().get(edgeId);
            }
        }

        public TEdge getEdge() {
            return edge;
        }

        public void setEdge(TEdge edge) {
            this.edge = edge;
        }
    }


    @DomEvent("vcf-network-open-node-editor")
    public static class NetworkOpenNodeEditorEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {
        private TNode node;

        public NetworkOpenNodeEditorEvent(Network<TNode,?> source, boolean fromClient,
                                             @EventData("event.detail.id") String nodeId,
                                             @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            if (source.getCurrentData().getNodes().containsKey(nodeId)){
                node = source.getCurrentData().getNodes().get(nodeId);
            }
        }

        public TNode getNode() {
            return node;
        }

        public void setNode(TNode node) {
            this.node = node;
        }
    }


    @DomEvent("vcf-network-save-node-editor")
    public static class NetworkSaveNodeEditorEvent extends ComponentEvent<Network> {

        public NetworkSaveNodeEditorEvent(Network source, boolean fromClient,
                                          @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
        }

    }

    @DomEvent("vcf-network-navigate-to")
    public static class NetworkNavigateToEvent extends ComponentEvent<Network> {
        private String nodeId;

        public NetworkNavigateToEvent(Network source, boolean fromClient,
                                          @EventData("event.detail.id") String nodeId,
                                          @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            // Root is selected
            if (nodeId == null || nodeId.isEmpty()){
                this.nodeId = null;
            } else {
                this.nodeId = nodeId;
            }
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
    }



    @DomEvent("vcf-network-new-template")
    public static class NetworkNewTemplateEvent extends ComponentEvent<Network> {

        public NetworkNewTemplateEvent(Network source, boolean fromClient,
                                          @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
        }
    }


    @DomEvent("vcf-network-update-template")
    public static class NetworkUpdateTemplateEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {

        private TNode template;

        public NetworkUpdateTemplateEvent(Network<TNode, TEdge> source, boolean fromClient,
                                          @EventData("event.detail.id") String templateId,
                                          @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            if (source.getTemplates().containsKey(templateId)){
                template = source.getTemplates().get(templateId);
            }
        }

        public TNode getTemplate() {
            return template;
        }

        public void setTemplate(TNode template) {
            this.template = template;
        }
    }

    @DomEvent("vcf-network-delete-template")
    public static class NetworkDeleteTemplateEvent<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends ComponentEvent<Network> {

        private TNode template;

        public NetworkDeleteTemplateEvent(Network<TNode, TEdge> source, boolean fromClient,
                                          @EventData("event.detail.id") String templateId,
                                          @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            if (source.getTemplates().containsKey(templateId)){
                template = source.getTemplates().get(templateId);
            }
        }

        public TNode getTemplate() {
            return template;
        }

        public void setTemplate(TNode template) {
            this.template = template;
        }
    }


    @FunctionalInterface
    public interface ConfirmEventListener<T extends ComponentEvent<?>>
            extends EventListener, Serializable {

        boolean onConfirmEvent(T event);
    }

}
