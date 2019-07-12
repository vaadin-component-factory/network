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
import com.vaadin.componentfactory.editor.NetworkNodeEditor;
import com.vaadin.componentfactory.event.NetworkEvent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Network main class.
 * Java Wrapper for the vcf-network component
 *
 * @param <TNode> Node type
 * @param <TEdge> Edge type
 */
@Tag("vcf-network")
@NpmPackage(value="@vaadin-component-factory/vcf-network", version="1.0.0-alpha.4")
@JsModule("@vaadin-component-factory/vcf-network/src/vcf-network.js")
public class Network<TNode extends NetworkNode<TNode, TEdge>, TEdge extends NetworkEdge> extends Component implements HasSize, HasTheme {

    private TNode rootData;
    /**
     * current data displayed , by default it's the rootData
     **/
    private TNode currentData;

    private final Class<TNode> nodeClass;
    private final Class<TEdge> edgeClass;

    private final NetworkConverter<TNode, TEdge> networkConverter;

    private NetworkNodeEditor<TNode, TEdge> nodeEditor;

    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent>> deleteNodesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent>> deleteEdgesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode, TEdge>>> newNodesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>>> newEdgesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateNodesEvent<TNode, TEdge>>> updateNodesListeners = new LinkedHashSet<>();
    private final Set<NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateEdgesEvent<TEdge>>> updateEdgesListeners = new LinkedHashSet<>();
    private final Set<ComponentEventListener<NetworkEvent.NetworkNewComponentEvent<TNode,TEdge>>> newComponentListeners = new LinkedHashSet<>();
    private ComponentEventListener<NetworkEvent.NetworkAfterDeleteNodesEvent> afterDeleteNodesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterNewNodesEvent<TNode, TEdge>> afterNewNodesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterDeleteEdgesEvent> afterDeleteEdgesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterNewEdgesEvent<TEdge>> afterNewEdgesListener;


    public Network(Class<TNode> nodeClass, Class<TEdge> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
        try {
            rootData = nodeClass.getDeclaredConstructor().newInstance();
            currentData = rootData;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        networkConverter = new NetworkConverter<>(this.nodeClass, this.edgeClass);
        registerHandlers();
    }

    public NetworkConverter<TNode, TEdge> getNetworkConverter() {
        return networkConverter;
    }

    @SuppressWarnings("unchecked")
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
                                for (String id : e.getNetworkNodesId()) {
                                    currentData.getNodes().remove(id);
                                }
                                // refresh the client side
                                getElement().callJsFunction("confirmDeleteNodes", NetworkConverter.convertIdListToJson(e.getNetworkNodesId()));
                                // call afterDelete
                                if (afterDeleteNodesListener != null) {
                                    afterDeleteNodesListener.onComponentEvent(new NetworkEvent.NetworkAfterDeleteNodesEvent(this, e.getNetworkNodesId()));
                                }
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
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
                                for (String id : e.getNetworkEdgesId()) {
                                    currentData.getEdges().remove(id);
                                }
                                // refresh the client side
                                getElement().callJsFunction("confirmDeleteEdges", NetworkConverter.convertIdListToJson(e.getNetworkEdgesId()));
                                // call afterDelete
                                if (afterDeleteEdgesListener != null) {
                                    afterDeleteEdgesListener.onComponentEvent(new NetworkEvent.NetworkAfterDeleteEdgesEvent(this, e.getNetworkEdgesId()));
                                }
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                })
        );


        ComponentUtil.addListener(this, NetworkEvent.NetworkNewNodesEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkNewNodesEvent<TNode, TEdge>>) e -> {
                    if (!e.getNetworkNodes().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode, TEdge>> listener : newNodesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                for (TNode networkNode : e.getNetworkNodes()) {
                                    currentData.getNodes().put(networkNode.getId(), networkNode);
                                }
                                // refresh the client side
                                getElement().callJsFunction("confirmAddNodes", NetworkConverter.convertNetworkNodeListToJsonArray(e.getNetworkNodes()));
                                if (afterNewNodesListener != null) {
                                    afterNewNodesListener.onComponentEvent(new NetworkEvent.NetworkAfterNewNodesEvent<>(this, e.getNetworkNodes()));
                                }
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                })
        );

        ComponentUtil.addListener(this, NetworkEvent.NetworkNewEdgesEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>>) e -> {
                    if (!e.getNetworkEdges().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>> listener : newEdgesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                currentData.getEdges().putAll(e.getNetworkEdges().stream().collect(Collectors.toMap(TEdge::getId, edge -> edge)));
                                // refresh the client side
                                getElement().callJsFunction("confirmAddEdges", NetworkConverter.convertNetworkEdgeListToJsonArray(e.getNetworkEdges()));
                                // call afterNew
                                if (afterNewEdgesListener != null) {
                                    afterNewEdgesListener.onComponentEvent(new NetworkEvent.NetworkAfterNewEdgesEvent<>(this, e.getNetworkEdges()));
                                }
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                })
        );

        ComponentUtil.addListener(this, NetworkEvent.NetworkUpdateNodesEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkUpdateNodesEvent<TNode, TEdge>>) e -> {
                    if (!e.getNetworkNodes().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateNodesEvent<TNode, TEdge>> listener : updateNodesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                // replace the updated nodes
                                for (TNode networkNode : e.getNetworkNodes()) {
                                    currentData.getNodes().put(networkNode.getId(), networkNode);
                                }
                                // refresh the client side
                                getElement().callJsFunction("confirmUpdateNodes", NetworkConverter.convertNetworkNodeListToJsonArray(e.getNetworkNodes()));
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                })
        );
        ComponentUtil.addListener(this, NetworkEvent.NetworkUpdateEdgesEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkUpdateEdgesEvent<TEdge>>) e -> {
                    if (!e.getNetworkEdges().isEmpty()) {
                        try {
                            boolean confirmed = true;
                            for (NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateEdgesEvent<TEdge>> listener : updateEdgesListeners) {
                                confirmed &= listener.onConfirmEvent(e);
                            }
                            if (confirmed) {
                                // Remove all the nodes with the same id and replace it with the updated nodes
                                for (TEdge networkEdge : e.getNetworkEdges()) {
                                    currentData.getEdges().put(networkEdge.getId(), networkEdge);
                                }
                                // refresh the client side
                                getElement().callJsFunction("confirmUpdateEdges", NetworkConverter.convertNetworkEdgeListToJsonArray(e.getNetworkEdges()));
                            }
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                })
        );
        ComponentUtil.addListener(this, NetworkEvent.NetworkNewComponentEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkNewComponentEvent<TNode,TEdge>>) e -> {
                    newComponentListeners.forEach(listener -> listener.onComponentEvent(e));
                })
        );

        ComponentUtil.addListener(this, NetworkEvent.NetworkNavigateToEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkNavigateToEvent>) e ->
                    navigateTo(e.getNodeId()))
        );


        ComponentUtil.addListener(this, NetworkEvent.NetworkUpdateCoordinatesEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkUpdateCoordinatesEvent>) e -> {
                    // update the node coordinates in the model
                    if (getCurrentData().getNodes().containsKey(e.getNodeId())){
                        TNode nodeToMove = getCurrentData().getNodes().get(e.getNodeId());
                        nodeToMove.setX(e.getX());
                        nodeToMove.setY(e.getY());
                        // send the updated node to the client
                        updateNode(nodeToMove);
                    }
                })
        );

    }

    /**
     * @return the current zoom
     */
    @Synchronize(property = "scale", value = "vcf-network-scale-changed")
    public double getScale() {
        return getElement().getProperty("scale", 1.33);
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
     * @return list of nodes
     */
    public Collection<TNode> getNodes() {
        return currentData.getNodes().values();
    }

    /**
     * Add a new Network node
     * If you want to add multiple nodes then call {@link #addNodes(List)}
     *
     * @param node the node to add
     */
    public void addNode(TNode node) {
        List<TNode> nodes = new ArrayList<>();
        nodes.add(node);
        addNodes(nodes);
    }

    /**
     * Add all the nodes in the network
     *
     * @param networkNodes nodes to add
     */
    public void addNodes(List<TNode> networkNodes) {
        for (TNode networkNode : networkNodes) {
            currentData.getNodes().put(networkNode.getId(), networkNode);
        }
        getElement().callJsFunction("confirmAddNodes", NetworkConverter.convertNetworkNodeListToJsonArray(networkNodes));
    }

    /**
     * remove the node from the network
     * If you want to remove multiple nodes then call {@link #deleteNodes(List)}
     *
     * @param node node to delete
     */
    public void deleteNode(TNode node) {
        List<TNode> nodes = new ArrayList<>();
        nodes.add(node);
        deleteNodes(nodes);
    }

    /**
     * remove all the nodes from the network
     *
     * @param nodes nodes to delete
     */
    public void deleteNodes(List<TNode> nodes) {
        for (TNode node : nodes) {
            currentData.getNodes().remove(node.getId());
        }
        getElement().callJsFunction("confirmDeleteNodes", networkConverter.convertNetworkNodeListToJsonArrayOfIds(nodes));
    }

    /**
     * Update the node attributes
     * If you want to update multiple nodes then call {@link #updateNodes(List)}
     *
     * @param node node to be updated
     */
    public void updateNode(TNode node) {
        List<TNode> nodes = new ArrayList<>();
        nodes.add(node);
        updateNodes(nodes);
    }

    /**
     * Update the node attributes
     *
     * @param networkNodes nodes to update
     */
    public void updateNodes(List<TNode> networkNodes) {
        getElement().callJsFunction("confirmUpdateNodes", NetworkConverter.convertNetworkNodeListToJsonArray(networkNodes));
    }

    /**
     * Add a new edge between 2 network edges
     *
     * @param edge edge to add
     */

    public void addEdge(TEdge edge) {
        List<TEdge> edges = new ArrayList<>();
        edges.add(edge);
        addEdges(edges);
    }

    /**
     * Add all the edges in the network
     *
     * @param networkEdges edges to add
     */
    public void addEdges(List<TEdge> networkEdges) {
        for (TEdge networkEdge : networkEdges) {
            currentData.getEdges().put(networkEdge.getId(), networkEdge);
        }
        getElement().callJsFunction("confirmAddEdges", NetworkConverter.convertNetworkEdgeListToJsonArray(networkEdges));
    }

    /**
     * Delete the edge with the same id
     * If you want to remove multiple edges then call {@link #deleteEdges(List)}
     *
     * @param edge edge to delete
     */
    public void deleteEdge(TEdge edge) {
        List<TEdge> edges = new ArrayList<>();
        edges.add(edge);
        deleteEdges(edges);
    }

    /**
     * Delete the edges with the same id
     *
     * @param edges list of edges
     */
    public void deleteEdges(List<TEdge> edges) {
        for (TEdge edge : edges) {
            currentData.getEdges().remove(edge.getId());
        }
        getElement().callJsFunction("confirmDeleteEdges", networkConverter.convertNetworkEdgeListToJsonArrayOfIds(edges));
    }

    /**
     *
     * @return list of edges
     */
    public Collection<TEdge> getEdges() {
        return currentData.getEdges().values();
    }

    /**
     * Select the elements with the same id
     *
     * @param networkNodes list of network nodes
     * @param networkEdges list of network edges
     */
    public void select(Collection<TNode> networkNodes, Collection<TEdge> networkEdges) {
        getElement().callJsFunction("select", networkConverter.convertNetworkNodeListToJsonArrayOfIds(networkNodes), networkConverter.convertNetworkEdgeListToJsonArrayOfIds(networkEdges));
    }

    public TNode getRootData() {
        return rootData;
    }

    public TNode getCurrentData() {
        return currentData;
    }

    /**
     * Change the currentData context
     *
     * @param id id of the context node
     */
    private void navigateTo(String id){
        // no id so it's root data
        if (id == null) {
            currentData = rootData;
            // check if the user double-click on a node (in the node list of the current Data)
        } else if (currentData.getNodes().containsKey(id)){
            currentData = currentData.getNodes().get(id);
        } else {
            //
            currentData = rootData.findNodeById(id);
        }
        if (currentData == null) {
            // id not found select rootdata
            currentData = rootData;
        }
    }

    /**
     * Add a custom editor and remove the default behaviour
     *
     * @param editor custom editor for a node
     */
    @SuppressWarnings("unchecked")
    public void addNodeEditor(NetworkNodeEditor<TNode, TEdge> editor) {
        this.nodeEditor = editor;
        editor.getView().getElement().setAttribute("slot", "node-form");
        getElement().appendChild(editor.getView().getElement());

        // register open-node-editor with an ID
        ComponentUtil.addListener(this, NetworkEvent.NetworkOpenNodeEditorEvent.class, (ComponentEventListener)
                ((ComponentEventListener<NetworkEvent.NetworkOpenNodeEditorEvent<TNode, TEdge>>) e -> {
                    // get the node from the ID or create a new one
                    try {
                        TNode editedNode = e.getNode();
                        if (editedNode == null) {
                            editedNode = nodeClass.getDeclaredConstructor().newInstance();
                        }
                        nodeEditor.readBean(editedNode);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                        ex.printStackTrace();
                    }

                })
        );

        // register save-node-editor
        ComponentUtil.addListener(this, NetworkEvent.NetworkSaveNodeEditorEvent.class,
                ((ComponentEventListener<NetworkEvent.NetworkSaveNodeEditorEvent>) e -> {
                    // get the node from the ID or create a new one
                    if (nodeEditor.writeBeanIfValid(nodeEditor.getBean())) {
                        TNode savedBean = nodeEditor.save(nodeEditor.getBean());
                        // update the client side
                        updateNode(savedBean);
                    }
                })
        );
    }

    // ALL THE LISTENERS

    public Registration addDeleteNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent> listener) {
        deleteNodesListeners.add(listener);
        return () -> deleteNodesListeners.remove(listener);
    }

    public Registration addDeleteEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent> listener) {
        deleteEdgesListeners.add(listener);
        return () -> deleteEdgesListeners.remove(listener);
    }

    public Registration addNewNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode, TEdge>> listener) {
        newNodesListeners.add(listener);
        return () -> newNodesListeners.remove(listener);
    }

    public Registration addNewEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>> listener) {
        newEdgesListeners.add(listener);
        return () -> newEdgesListeners.remove(listener);
    }

    public Registration addNewComponentListener(ComponentEventListener<NetworkEvent.NetworkNewComponentEvent<TNode,TEdge>> listener) {
        newComponentListeners.add(listener);
        return () -> newComponentListeners.remove(listener);
    }

    public Registration addUpdateNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateNodesEvent<TNode, TEdge>> listener) {
        updateNodesListeners.add(listener);
        return () -> updateNodesListeners.remove(listener);
    }

    public Registration addUpdateEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateEdgesEvent<TEdge>> listener) {
        updateEdgesListeners.add(listener);
        return () -> updateEdgesListeners.remove(listener);
    }

    /**
     * Adds a selection listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    @SuppressWarnings("unchecked")
    public Registration addNetworkSelectionListener(ComponentEventListener<NetworkEvent.NetworkSelectionEvent<TNode, TEdge>> listener) {
        return addListener(NetworkEvent.NetworkSelectionEvent.class, (ComponentEventListener) listener);
    }

    @SuppressWarnings("unchecked")
    public Registration addHoverNodeListener(ComponentEventListener<NetworkEvent.NetworkHoverNodeEvent<TNode, TEdge>> listener) {
        return addListener(NetworkEvent.NetworkHoverNodeEvent.class, (ComponentEventListener) listener);
    }

    @SuppressWarnings("unchecked")
    public Registration addHoverEdgeListener(ComponentEventListener<NetworkEvent.NetworkHoverEdgeEvent<TEdge>> listener) {
        return addListener(NetworkEvent.NetworkHoverEdgeEvent.class, (ComponentEventListener) listener);
    }

    public Registration addNetworkAfterDeleteNodesListener(ComponentEventListener<NetworkEvent.NetworkAfterDeleteNodesEvent> listener) {
        afterDeleteNodesListener = listener;
        return addListener(NetworkEvent.NetworkAfterDeleteNodesEvent.class, listener);
    }

    @SuppressWarnings("unchecked")
    public Registration addNetworkAfterNewNodesListener(ComponentEventListener<NetworkEvent.NetworkAfterNewNodesEvent<TNode, TEdge>> listener) {
        afterNewNodesListener = listener;
        return addListener(NetworkEvent.NetworkAfterNewNodesEvent.class, (ComponentEventListener) listener);
    }

    public Registration addNetworkAfterDeleteEdgesListener(ComponentEventListener<NetworkEvent.NetworkAfterDeleteEdgesEvent> listener) {
        afterDeleteEdgesListener = listener;
        return addListener(NetworkEvent.NetworkAfterDeleteEdgesEvent.class, listener);
    }

    @SuppressWarnings("unchecked")
    public Registration addNetworkAfterNewEdgesListener(ComponentEventListener<NetworkEvent.NetworkAfterNewEdgesEvent<TEdge>> listener) {
        afterNewEdgesListener = listener;
        return addListener(NetworkEvent.NetworkAfterNewEdgesEvent.class, (ComponentEventListener) listener);
    }
}
