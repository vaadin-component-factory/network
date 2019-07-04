package com.vaadin.componentfactory;

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

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.event.NetworkEvent;
import com.vaadin.componentfactory.model.AbstractNetworkComponent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


@Tag("vcf-network")
@JsModule("./src/vcf-network.js")
public class Network<TComponent extends AbstractNetworkComponent<TNode,TEdge>,TNode extends NetworkNode,TEdge extends NetworkEdge> extends Component implements HasSize, HasTheme {

    private TComponent rootData;

    private List<TNode> selectedNodes = new ArrayList<>();
    private List<NetworkEdge> selectedEdges = new ArrayList<>();

    private final Class<TNode> nodeClass;
    private final Class<TEdge> edgeClass;
    private final Class<TComponent> componentClass;

    private final NetworkConverter<TComponent, TNode, TEdge> networkConverter;

    public Network(Class<TComponent> componentClass,Class<TNode> nodeClass, Class<TEdge> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
        this.componentClass = componentClass;
        try {
            rootData = componentClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        networkConverter = new NetworkConverter<>(this.componentClass, this.nodeClass, this.edgeClass);
        registerHandlers();
    }

    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent>> deleteNodesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent>> deleteEdgesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode>>> newNodesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>>> newEdgesListeners = new LinkedHashSet<>();
    private final Set<ComponentEventListener<NetworkEvent.NetworkNewComponentEvent>> newComponentListeners = new LinkedHashSet<>();

    public NetworkConverter<TComponent, TNode, TEdge> getNetworkConverter() {
        return networkConverter;
    }

    private void registerHandlers() {

        ComponentUtil.addListener(this, NetworkEvent.NetworkDeleteNodesEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkDeleteNodesEvent>) e -> {
                    if (!e.getNetworkNodesId().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent> listener : deleteNodesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                List<TNode> nodeToRemove = new ArrayList<>();
                                for (TNode node : rootData.getNodes()) {
                                    if (e.getNetworkNodesId().contains(node.getId())) {
                                        nodeToRemove.add(node);
                                    }
                                }
                                rootData.getNodes().removeAll(nodeToRemove);
                                // refresh the client side
                                getElement().callJsFunction("confirmDeleteNodes", NetworkConverter.convertIdListToJson(e.getNetworkNodesId()));
                            }
                        } catch (Exception exc ) {
                            exc.printStackTrace();
                        } finally {
                        }
                    }
                })
        );

        ComponentUtil.addListener(this, NetworkEvent.NetworkDeleteEdgesEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkDeleteEdgesEvent>) e -> {
                    if (!e.getNetworkEdgesId().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent> listener : deleteEdgesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                List<TEdge> edgeToRemove = new ArrayList<>();
                                for (TEdge edge : rootData.getEdges()) {
                                    if (e.getNetworkEdgesId().contains(edge.getId())) {
                                        edgeToRemove.add(edge);
                                    }
                                }
                                rootData.getEdges().removeAll(edgeToRemove);
                                // refresh the client side
                                getElement().callJsFunction("confirmDeleteEdges", NetworkConverter.convertIdListToJson(e.getNetworkEdgesId()));
                            }
                        } catch (Exception exc ) {
                            exc.printStackTrace();
                        } finally {
                        }
                    }
                })
        );


        ComponentUtil.addListener(this, NetworkEvent.NetworkNewNodesEvent.class,(ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkNewNodesEvent<TNode>>) e -> {
                    if (!e.getNetworkNodes().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode>> listener : newNodesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                rootData.getNodes().addAll(e.getNetworkNodes());
                                // refresh the client side
                                getElement().callJsFunction("confirmAddNodes", networkConverter.convertNetworkNodeListToJsonArray(e.getNetworkNodes()));
                            }
                        } catch (Exception exc ) {
                            exc.printStackTrace();
                        } finally {
                        }
                    }
                })
        );



        ComponentUtil.addListener(this, NetworkEvent.NetworkNewEdgesEvent.class,(ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>>) e -> {
                    if (!e.getNetworkEdges().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>> listener : newEdgesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                rootData.getEdges().addAll(e.getNetworkEdges());
                                // refresh the client side
                                getElement().callJsFunction("confirmAddEdges", networkConverter.convertNetworkEdgeListToJsonArray(e.getNetworkEdges()));
                            }
                        } catch (Exception exc ) {
                            exc.printStackTrace();
                        } finally {
                        }
                    }
                })
        );

        ComponentUtil.addListener(this, NetworkEvent.NetworkNewComponentEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkNewComponentEvent>) e -> {
                    newComponentListeners.forEach(listener -> listener.onComponentEvent(e));
                })
        );
    }

    public Registration addDeleteNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent> listener) {
        deleteNodesListeners.add(listener);
        return () -> deleteNodesListeners.remove(listener);
    }

    public Registration addDeleteEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent> listener) {
        deleteEdgesListeners.add(listener);
        return () -> deleteEdgesListeners.remove(listener);
    }

    public Registration addNewNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode>> listener) {
        newNodesListeners.add(listener);
        return () -> newNodesListeners.remove(listener);
    }
    public Registration addNewEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>> listener) {
        newEdgesListeners.add(listener);
        return () -> newEdgesListeners.remove(listener);
    }
    public Registration addNewComponentListener(ComponentEventListener<NetworkEvent.NetworkNewComponentEvent> listener) {
        newComponentListeners.add(listener);
        return () -> newEdgesListeners.remove(listener);
    }
    /**
     * Set the scale of the network
     *
     * @param scale zoom
     */
    public void setScale(double scale) {
        getElement().setProperty("scale", scale);
    }

    /**
     *
     * @return the current zoom
     */
    @Synchronize(property = "scale",value = "vcf-network-scale-changed")
    public double getScale() {
        return getElement().getProperty("scale", 1.33);
    }

    public List<TNode> getNodes() {
        return rootData.getNodes();
    }
    /**
     * Add a new Network node
     *
     * @param node the node to add
     */
    public void addNode(TNode node){
        List<TNode> nodes = new ArrayList<>();
        nodes.add(node);
        addNodes(nodes);
    }

    public void addNodes(List<TNode> networkNodes){
        rootData.getNodes().addAll(networkNodes);
        getElement().callJsFunction("confirmAddNodes", networkConverter.convertNetworkNodeListToJsonArray(networkNodes));
    }

    // Call vcf-network-delete
    public void deleteNode(TNode node){
        List<TNode> nodes = new ArrayList<>();
        nodes.add(node);
        deleteNodes(nodes);
    }

    // duplicate the client side logic into this function
    public void deleteNodes(List<TNode> nodes){
        rootData.getNodes().removeAll(nodes);
        getElement().callJsFunction("confirmDeleteNodes", networkConverter.convertNetworkNodeListToJsonArrayOfIds(nodes));
    }

    /**
     * Update the node attributes
     *
     * @param node node to be updated
     */
    public void updateNode(TNode node) {
        getElement().callJsFunction("updateNode", node.toJson());
    }

    /**
     * Add a new edge between 2 network edges
     *
     * @param edge edge to add
     */

    public void addEdge(TEdge edge){
        List<TEdge> edges = new ArrayList<>();
        edges.add(edge);
        addEdges(edges);
    }

    public void addEdges(List<TEdge> networkEdges){
        rootData.getEdges().addAll(networkEdges);
        getElement().callJsFunction("confirmAddEdges", networkConverter.convertNetworkEdgeListToJsonArray(networkEdges));
    }

    /**
     * Delete the edge with the same id
     *
     * @param edge edge to delete
     */
    public void deleteEdge(TEdge edge){
        List<TEdge> edges = new ArrayList<>();
        edges.add(edge);
        deleteEdges(edges);
    }

    // duplicate the client side logic into this function
    public void deleteEdges(List<TEdge> edges){
        rootData.getEdges().removeAll(edges);
        getElement().callJsFunction("confirmDeleteEdges", networkConverter.convertNetworkEdgeListToJsonArrayOfIds(edges));
    }
    /**
     * Create a component with all selected nodes
     * It does includes all the edges automatically
     *
     * @param nodes list of nodes
     */
    public void createComponent(List<TNode> nodes) {
        // getElement().callJsFunction("select", NetworkConverter.convertNetworkNodeListToJsonArrayOfIds(nodes));
    }

    public List<TEdge> getEdges() {
        return rootData.getEdges();
    }

    /**
     * Select the elements
     *
     * @param networkNodes list of network nodes
     * @param networkEdges list of network edges
     */
    public void select(List<TNode> networkNodes, List<TEdge> networkEdges){
        getElement().callJsFunction("select", networkConverter.convertNetworkNodeListToJsonArrayOfIds(networkNodes), networkConverter.convertNetworkEdgeListToJsonArrayOfIds(networkEdges));
    }
    /**
     * Adds a selection listener to this component.
     *
     * @param listener
     *            the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addNetworkSelectionListener(ComponentEventListener<NetworkEvent.NetworkSelectionEvent> listener) {
        return addListener(NetworkEvent.NetworkSelectionEvent.class, listener);
    }

    public AbstractNetworkComponent getRootData() {
        return rootData;
    }
}
