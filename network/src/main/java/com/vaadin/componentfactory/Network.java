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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.converter.NetworkConverter;
import com.vaadin.componentfactory.editor.NetworkNodeEditor;
import com.vaadin.componentfactory.event.NetworkEvent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.componentfactory.model.NetworkNode.ComponentColor;
import com.vaadin.componentfactory.model.NetworkNode.NodeType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Network main class.
 * Java Wrapper for the vcf-network component
 *
 * @param <TNode> Node type
 * @param <TEdge> Edge type
 *
 * @author Vaadin Ltd
 */
@Tag("vcf-network")
@NpmPackage(value="@vaadin-component-factory/vcf-network", version="1.0.0-beta.2")
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
    private ComponentEventListener<NetworkEvent.NetworkAfterDeleteNodesEvent> afterDeleteNodesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterNewNodesEvent<TNode, TEdge>> afterNewNodesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterDeleteEdgesEvent> afterDeleteEdgesListener;
    private ComponentEventListener<NetworkEvent.NetworkAfterNewEdgesEvent<TEdge>> afterNewEdgesListener;

    private final Map<String, TNode> templates = new HashMap<>();

    public Network(Class<TNode> nodeClass, Class<TEdge> edgeClass) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
        try {
            rootData = nodeClass.getDeclaredConstructor().newInstance();
            currentData = rootData;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate new bean", e);
        }
        networkConverter = new NetworkConverter<>(this.nodeClass, this.edgeClass);
        registerHandlers();
    }


    public Network(Class<TNode> nodeClass, Class<TEdge> edgeClass, TNode rootData) {
        this.nodeClass = nodeClass;
        this.edgeClass = edgeClass;
        this.rootData = rootData;
        this.currentData = rootData;
        networkConverter = new NetworkConverter<>(this.nodeClass, this.edgeClass);
        registerHandlers();

        getElement().callJsFunction("confirmAddNodes", NetworkConverter.convertNetworkNodeListToJsonArray(rootData.getNodes().values()));
        getElement().callJsFunction("confirmAddEdges", NetworkConverter.convertNetworkEdgeListToJsonArray(rootData.getEdges().values()));
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
                    createComponent(e.getNetworkNodes());
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



        ComponentUtil.addListener(this, NetworkEvent.NetworkDeleteTemplateEvent.class, (ComponentEventListener)
                (ComponentEventListener<NetworkEvent.NetworkDeleteTemplateEvent<TNode,TEdge>>) e -> {
                    ConfirmDialog dialog = new ConfirmDialog("Confirm delete",
                            "Are you sure you want to delete the template?", "Yes", event -> {deleteTemplate(e.getTemplate());event.getSource().close();},
                            "Cancel", event -> {event.getSource().close();});
                    dialog.open();
                }
        );
        ComponentUtil.addListener(this, NetworkEvent.NetworkNewTemplateEvent.class,
                (ComponentEventListener<NetworkEvent.NetworkNewTemplateEvent>) e ->
                    addNewTemplate()
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
     * If you want to add multiple nodes then call {@link #addNodes(Collection)}
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
    public void addNodes(Collection<TNode> networkNodes) {
        for (TNode networkNode : networkNodes) {
            currentData.getNodes().put(networkNode.getId(), networkNode);
        }
        getElement().callJsFunction("confirmAddNodes", NetworkConverter.convertNetworkNodeListToJsonArray(networkNodes));
    }

    /**
     * remove the node from the network
     * If you want to remove multiple nodes then call {@link #deleteNodes(Collection)}
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
    public void deleteNodes(Collection<TNode> nodes) {
        for (TNode node : nodes) {
            currentData.getNodes().remove(node.getId());
        }
        getElement().callJsFunction("confirmDeleteNodes", networkConverter.convertNetworkNodeListToJsonArrayOfIds(nodes));
    }

    /**
     * Update the node attributes
     * If you want to update multiple nodes then call {@link #updateNodes(Collection)}
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
    public void updateNodes(Collection<TNode> networkNodes) {
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
    public void addEdges(Collection<TEdge> networkEdges) {
        for (TEdge networkEdge : networkEdges) {
            currentData.getEdges().put(networkEdge.getId(), networkEdge);
        }
        getElement().callJsFunction("confirmAddEdges", NetworkConverter.convertNetworkEdgeListToJsonArray(networkEdges));
    }

    /**
     * Delete the edge with the same id
     * If you want to remove multiple edges then call {@link #deleteEdges(Collection)}
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
    public void deleteEdges(Collection<TEdge> edges) {
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
        JsonObject jsonObject = Json.createObject();
        jsonObject.put("nodeIds", networkConverter.convertNetworkNodeListToJsonArrayOfIds(networkNodes));
        jsonObject.put("edgeIds", networkConverter.convertNetworkEdgeListToJsonArrayOfIds(networkEdges));
        getElement().callJsFunction("select", jsonObject);
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
    /////// Templates

    /**
     *
     * @return list of template
     */
    public Map<String, TNode> getTemplates() {
        return templates;
    }

    /**
     * This action is called when a user click on "+ New template"
     * You can overrides it if you want different default values
     */
    public void addNewTemplate() {
        try {
            TNode template = nodeClass.getDeclaredConstructor().newInstance();
            template.setLabel("New template "+ templates.size());
            template.setType(NodeType.COMPONENT_TYPE);
            template.setComponentColor(ComponentColor.BLUE);
            addTemplate(template);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate new bean", e);
        }
    }

    /**
     * Add a template in the list
     * template.id should be unique in the list
     * @param template template to add
     */
    public void addTemplate(TNode template){
        templates.put(template.getId(),template);
        getElement().callJsFunction("confirmAddTemplate", template.toJson());
    }

    /**
     * Edit a template in the list
     *
     * @param template template to edit
     */
    public void updateTemplate(TNode template){
        templates.put(template.getId(),template);
        getElement().callJsFunction("confirmUpdateTemplate", template.toJson());
    }


    /**
     * Delete the template with the same id
     *
     * @param template template to delete
     */
    public void deleteTemplate(TNode template){
        templates.remove(template.getId());
        getElement().callJsFunction("confirmDeleteTemplate", template.getId());
    }

    ///// Templates
    /**
     * Check if nodes are linked to an external node then block the process if
     *
     * - remove all the selected nodes from the current Data
     * - remove all the edges linked to this nodes from the current Data
     * - create a new component node
     *   - add all the selected nodes into the component
     *   - add all the inner edges
     *   - add a input/output node if there is no input/output node
     *
     *   You can overrides the default behaviour if it does not fit your need
     *
     * @param networkNodes selected nodes
     */
    public void createComponent(List<TNode> networkNodes) {
        if (networkNodes.isEmpty()) {
            Notification.show("No node selected");
        } else {
            List<String> ids = networkNodes.stream().map(TNode::getId).collect(Collectors.toList());

            List<TEdge> edges = new ArrayList<>();

            boolean innerLinkedToOuter = false;
            for (TEdge edge : getCurrentData().getEdges().values()) {
                boolean nodeFromInner = ids.contains(edge.getFrom());
                boolean nodeToInner = ids.contains(edge.getTo());
                // edge is inner --> OK OR edge is outer
                if ((nodeFromInner && nodeToInner) || (!nodeFromInner && !nodeToInner)) {
                    innerLinkedToOuter = false;
                    // the edge is an inner edge
                    if (nodeFromInner)
                        edges.add(edge);
                } else {
                    innerLinkedToOuter = true;
                }
                if (innerLinkedToOuter) {
                    break;
                }
            }
            if (innerLinkedToOuter) {
                // error
                Notification.show("Some nodes are linked to outer nodes, please select not connected nodes");
            } else {
                // ok
                // remove edges and put it into the new component
                try {
                    TNode component = nodeClass.getDeclaredConstructor().newInstance();
                    component.setLabel("New component");
                    component.setType(NodeType.COMPONENT_TYPE);
                    // create a component as the same position of the 1st node
                    component.setX(networkNodes.get(0).getX());
                    component.setY(networkNodes.get(0).getY());
                    deleteNodes(networkNodes);
                    boolean hasInput = false;
                    boolean hasOutput = false;
                    for (TNode networkNode : networkNodes) {
                        component.getNodes().put(networkNode.getId(),networkNode);
                        if (NodeType.INPUT_TYPE.equals(networkNode.getType())){
                            hasInput = true;
                        }
                        if (NodeType.OUTPUT_TYPE.equals(networkNode.getType())){
                            hasOutput = true;
                        }
                    }
                    deleteEdges(edges);
                    for (TEdge edge : edges) {
                        component.getEdges().put(edge.getId(),edge);
                    }

                    // add input node if needed

                    if (!hasInput) {
                        TNode inputNode = nodeClass.getDeclaredConstructor().newInstance();
                        inputNode.setLabel("input");
                        inputNode.setType(NodeType.INPUT_TYPE);
                        inputNode.setX(-250);
                        component.getNodes().put(inputNode.getId(),inputNode);
                    }
                    // add output node if needed
                    if (!hasOutput) {
                        TNode outputNode = nodeClass.getDeclaredConstructor().newInstance();
                        outputNode.setLabel("output");
                        outputNode.setType(NodeType.OUTPUT_TYPE);
                        component.getNodes().put(outputNode.getId(),outputNode);
                    }

                    addNode(component);

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException("Unable to instantiate new bean", e);
                }
            }

        }
    }
    /// VISIBILITY

    /**
     * Show or hide the template panel
     * Default visible
     *
     * @param visible true to show or false to hide
     */
	public void setTemplatePanelVisible(boolean visible) {
    	getElement().callJsFunction(visible?"showTemplatePanel":"hideTemplatePanel");
	}

    /**
     * Open or close the right panel
     * Default opened
     *
     * @param opened true to open or false to close
     */
	public void setRightPanelOpened(boolean opened) {
		getElement().callJsFunction(opened?"openRightPanel":"closeRightPanel");
	}

    /**
     * Open or close the left panel
     * Default opened
     *
     * @param opened true to open or false to close
     */
	public void setLeftPanelOpened(boolean opened) {
		getElement().callJsFunction(opened?"openLeftPanel":"closeLeftPanel");
	}
	/// END VISIBILITY

    // ALL THE LISTENERS

    /**
     * Adds a delete node listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addDeleteNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteNodesEvent> listener) {
        deleteNodesListeners.add(listener);
        return () -> deleteNodesListeners.remove(listener);
    }

    /**
     * Adds a delete edge listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addDeleteEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkDeleteEdgesEvent> listener) {
        deleteEdgesListeners.add(listener);
        return () -> deleteEdgesListeners.remove(listener);
    }

    /**
     * Adds a new node listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addNewNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewNodesEvent<TNode, TEdge>> listener) {
        newNodesListeners.add(listener);
        return () -> newNodesListeners.remove(listener);
    }

    /**
     * Adds a new edge listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addNewEdgesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkNewEdgesEvent<TEdge>> listener) {
        newEdgesListeners.add(listener);
        return () -> newEdgesListeners.remove(listener);
    }

    /**
     * Adds a update node listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
    public Registration addUpdateNodesListener(NetworkEvent.ConfirmEventListener<NetworkEvent.NetworkUpdateNodesEvent<TNode, TEdge>> listener) {
        updateNodesListeners.add(listener);
        return () -> updateNodesListeners.remove(listener);
    }

    /**
     * Adds a update edge listener to this component.
     *
     * @param listener the listener to add, not <code>null</code>
     * @return a handle that can be used for removing the listener
     */
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


	/**
	 * Add an action when the user click on edit template
	 * The button is not shown if no listener is attached
	 *
	 * @param listener the listener to add, not <code>null</code>
	 * @return a handle that can be used for removing the listener
	 */
	@SuppressWarnings("unchecked")
    public Registration addNetworkUpdateTemplateListener(ComponentEventListener<NetworkEvent.NetworkUpdateTemplateEvent<TNode,TEdge>> listener) {
    	// show edit button
        getElement().callJsFunction("showEditTemplateButton");
        return addListener(NetworkEvent.NetworkUpdateTemplateEvent.class, (ComponentEventListener) listener);
    }
}
