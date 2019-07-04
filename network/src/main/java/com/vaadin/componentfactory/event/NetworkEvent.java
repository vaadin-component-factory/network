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
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import java.io.Serializable;
import java.util.EventListener;
import java.util.List;

public class NetworkEvent {

    private static final String EVENT_PREVENT_DEFAULT_JS = "event.preventDefault()";

    @DomEvent("vcf-network-selection")
    public static class NetworkSelectionEvent extends ComponentEvent<Network> {
        private List<NetworkNode> networkNodes;
        private List<NetworkEdge> networkEdges;

        public NetworkSelectionEvent(Network source, boolean fromClient,
                                     @EventData("event.detail.nodes") JsonObject nodes,
                                     @EventData("event.detail.edges") JsonObject edges) {
            super(source, fromClient);

        }

        public List<NetworkNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<NetworkNode> networkNodes) {
            this.networkNodes = networkNodes;
        }

        public List<NetworkEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<NetworkEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }

    @DomEvent("vcf-network-new-nodes")
    public static class NetworkNewNodesEvent extends ComponentEvent<Network> {
        private List<NetworkNode> networkNodes;

        public NetworkNewNodesEvent(Network source, boolean fromClient,
                                       @EventData("event.detail.items") JsonValue items,
                                       @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);

            networkNodes = NetworkConverter.convertJsonToNetworkNodeList(items);
        }

        public List<NetworkNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<NetworkNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }


    @DomEvent("vcf-network-new-edges")
    public static class NetworkNewEdgesEvent extends ComponentEvent<Network> {
        private List<NetworkEdge> networkEdges;

        public NetworkNewEdgesEvent(Network source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdges = NetworkConverter.convertJsonToNetworkEdgeList(items);
        }

        public List<NetworkEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<NetworkEdge> networkEdges) {
            this.networkEdges = networkEdges;
        }
    }


    @DomEvent("vcf-network-after-new-nodes")
    public static class NetworkAfterNewNodesEvent extends ComponentEvent<Network> {
        private List<NetworkNode> networkNodes;

        public NetworkAfterNewNodesEvent(Network source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);

            networkNodes = NetworkConverter.convertJsonToNetworkNodeList(items);
        }

        public NetworkAfterNewNodesEvent(Network source, boolean fromClient,
                                         List<NetworkNode> networkNodes) {
            super(source, fromClient);
            this.networkNodes = networkNodes;
        }
        public List<NetworkNode> getNetworkNodes() {
            return networkNodes;
        }

        public void setNetworkNodes(List<NetworkNode> networkNodes) {
            this.networkNodes = networkNodes;
        }
    }


    @DomEvent("vcf-network-after-new-edges")
    public static class NetworkAfterNewEdgesEvent extends ComponentEvent<Network> {
        private List<NetworkEdge> networkEdges;

        public NetworkAfterNewEdgesEvent(Network source, boolean fromClient,
                                    @EventData("event.detail.items") JsonValue items,
                                    @EventData(EVENT_PREVENT_DEFAULT_JS) Object ignored) {
            super(source, fromClient);
            networkEdges = NetworkConverter.convertJsonToNetworkEdgeList(items);
        }

        public NetworkAfterNewEdgesEvent(Network source, boolean fromClient,
                                         List<NetworkEdge> networkEdges) {
            super(source, fromClient);
            this.networkEdges = networkEdges;
        }
        public List<NetworkEdge> getNetworkEdges() {
            return networkEdges;
        }

        public void setNetworkEdges(List<NetworkEdge> networkEdges) {
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
            networkNodesId = NetworkConverter. convertJsonToIdList(ids);
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
            networkEdgesId = NetworkConverter. convertJsonToIdList(ids);
        }

        public List<String> getNetworkEdgesId() {
            return networkEdgesId;
        }

        public void setNetworkEdgesId(List<String> networkEdgesId) {
            this.networkEdgesId = networkEdgesId;
        }
    }


    @DomEvent("vcf-network-create-component")
    public static class NetworkNewComponentEvent extends ComponentEvent<Network> {

        public NetworkNewComponentEvent(Network source, boolean fromClient) {
            super(source, fromClient);
        }
    }



    @FunctionalInterface
    public interface ConfirmEventListener<T extends ComponentEvent<?>>
            extends EventListener, Serializable {

        boolean onConfirmEvent(T event);
    }

}
