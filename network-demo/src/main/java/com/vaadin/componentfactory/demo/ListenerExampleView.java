package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("listener")
public class ListenerExampleView extends VerticalLayout {

    private Network<CustomNetworkNode, CustomNetworkEdge> network = new Network<>(CustomNetworkNode.class,CustomNetworkEdge.class);

    public ListenerExampleView() {
        network.setWidthFull();
        addAndExpand(network);
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // put some data in the graph
        CustomNetworkNode node = new CustomNetworkNode();
        node.setLabel("Node 1");
        node.setX(-150);
        node.setY(-80);
        node.setType(NetworkNode.INPUT_TYPE);
        network.addNode(node);


        CustomNetworkNode node2 = new CustomNetworkNode();
        node2.setLabel("Node 2");
        node2.setX(-50);
        node2.setY(-80);
        network.addNode(node2);


        CustomNetworkNode node3 = new CustomNetworkNode();
        node3.setLabel("Node 3");
        node3.setX(-50);
        node3.setY(0);
        network.addNode(node3);
        CustomNetworkNode ncomponent = new CustomNetworkNode();

        ncomponent.setLabel("Component");
        ncomponent.setX(0);
        ncomponent.setY(0);
        ncomponent.setType(NetworkNode.COMPONENT_TYPE);


        CustomNetworkNode node4 = new CustomNetworkNode();
        node4.setLabel("Node 4");
        node4.setX(-50);
        node4.setY(0);
        ncomponent.getNodes().put(node4.getId(),node4);
        network.addNode(ncomponent);

        CustomNetworkNode previousNode = null;
        for (CustomNetworkNode networkNode : network.getNodes()) {
            if (previousNode != null){
                CustomNetworkEdge networkEdge = new CustomNetworkEdge();
                networkEdge.setFrom(previousNode.getId());
                networkEdge.setTo(networkNode.getId());
                network.addEdge(networkEdge);
            }
            previousNode = networkNode;
        }
        network.addNetworkAfterDeleteNodesListener(event -> {
            Notification.show("Nodes deleted =" + event.getNetworkNodesId());

            Notification.show("Find all linked edges and delete it");
            List<CustomNetworkEdge> edges = network.getEdges().stream().filter(customNetworkEdge -> event.getNetworkNodesId().contains(customNetworkEdge.getTo())||event.getNetworkNodesId().contains(customNetworkEdge.getFrom())).collect(Collectors.toList());

            Notification.show(edges.size() +" edges found");
            // delete it
            network.deleteEdges(edges);

        });
        network.addNodeEditor(new CustomNetworkNodeEditorImpl());

        network.addNetworkAfterNewNodesListener(event -> {
            Notification.show("addNetworkAfterNewNodesListener Added =" + event.getNetworkNodes());
        });

      network.addNewEdgesListener(event -> {
          boolean nok = true;
          if (event.getNetworkEdges().size() == 1) {
              NetworkEdge edge = event.getNetworkEdges().get(0);
              nok = forbidEdgeCreation(edge);
              if (nok){
                  Notification.show("Cannot add an edge because there is an edge in the opposite direction");
              } else {
                  Notification.show("OK");
              }
          }
          return !nok;
         }
      );
        network.addDeleteNodesListener(event -> {
            boolean ok = event.getNetworkNodesId().size() <= 2;
            if (ok) {
                Notification.show("Can delete selected nodes ");
            } else {
                Notification.show("Cant delete more than 2 nodes");
            }
            return ok;
        });

        network.addDeleteEdgesListener(event -> {
            boolean ok = event.getNetworkEdgesId().size() <= 2;
            if (ok) {
                Notification.show("Can delete selected edges ");
            } else {
                Notification.show("Cant delete more than 2 edges");
            }
            return ok;
        });

        network.addNewNodesListener(event -> {
            event.getNetworkNodes().forEach(tempNode ->
                    tempNode.setLabel("SERVER - "+tempNode.getLabel()));
            event.getNetworkNodes().forEach(tempNode ->
                    tempNode.setCustomField("SERVER CUSTOM DATA "));
            return true;
        });

        network.addNetworkSelectionListener(event -> {
           Notification.show("Nodes selected "+ event.getNetworkNodes());
           Notification.show("Edges selected "+ event.getNetworkEdges());
        });

        Button addEdgesButton = new Button("add edges", e ->{
                System.out.println("adding Edge");

            CustomNetworkNode previousNode1 = null;
            for (CustomNetworkNode networkNode : network.getNodes()) {
                if (previousNode1 != null){
                    CustomNetworkEdge networkEdge = new CustomNetworkEdge();
                    networkEdge.setFrom(previousNode1.getId());
                    networkEdge.setTo(networkNode.getId());
                    network.addEdge(networkEdge);
                }
                previousNode1 = networkNode;
            }});

        Button checkAllButton = new Button("check all", e -> {
            System.out.println(network.getRootData().toJson().toString());
            Notification.show("Rootdata displayed in the log ");
        });
        Button checkNodesListButton = new Button("check nodes list", e -> {
            System.out.println("node list "+ network.getNodes());
            Notification.show("nodes "+ network.getNodes());
        });

        Button checkEdgesListButton = new Button("check edges list", e -> {
            System.out.println("edge list "+ network.getEdges());
            Notification.show("edges "+ network.getEdges());
        });
        add(new HorizontalLayout(addEdgesButton, checkAllButton,checkNodesListButton, checkEdgesListButton));

        network.addNewComponentListener(e -> createComponent(e.getNetworkNodes()));
    }

    /**
     * check if there is an edge from <- to
     *
     * @param edge new edge
     * @return true edge creation should be forbidden
     */
    private boolean forbidEdgeCreation(NetworkEdge edge) {
        return network.getEdges().stream()
                .filter(networkEdge ->
                        edge.getFrom().equals(networkEdge.getTo())
                ).anyMatch(networkEdge ->
                        edge.getTo().equals(networkEdge.getFrom())
                );
    }

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
     * @param networkNodes selected nodes
     */
    private void createComponent(List<CustomNetworkNode> networkNodes) {
        if (networkNodes.isEmpty()) {
            Notification.show("No node selected");
        } else {
            List<String> ids = networkNodes.stream().map(CustomNetworkNode::getId).collect(Collectors.toList());

            List<CustomNetworkEdge> edges = new ArrayList<>();

            boolean innerLinkedToOuter = false;
            for (CustomNetworkEdge edge : network.getCurrentData().getEdges().values()) {
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
                CustomNetworkNode component = new CustomNetworkNode();
                component.setLabel("New component");
                component.setType(NetworkNode.COMPONENT_TYPE);
                // create a component as the same position of the 1st node
                component.setX(networkNodes.get(0).getX());
                component.setY(networkNodes.get(0).getY());
                network.deleteNodes(networkNodes);
                boolean hasInput = false;
                boolean hasOutput = false;
                for (CustomNetworkNode networkNode : networkNodes) {
                    component.getNodes().put(networkNode.getId(),networkNode);
                    if (CustomNetworkNode.INPUT_TYPE.equals(networkNode.getType())){
                        hasInput = true;
                    }
                    if (CustomNetworkNode.OUTPUT_TYPE.equals(networkNode.getType())){
                        hasOutput = true;
                    }
                }
                network.deleteEdges(edges);
                for (CustomNetworkEdge edge : edges) {
                    component.getEdges().put(edge.getId(),edge);
                }

                // add input node if needed

                if (!hasInput) {
                    CustomNetworkNode inputNode = new CustomNetworkNode();
                    inputNode.setLabel("input");
                    inputNode.setType(NetworkNode.INPUT_TYPE);
                    inputNode.setX(-250);
                    component.getNodes().put(inputNode.getId(),inputNode);
                }
                // add output node if needed
                if (!hasOutput) {
                    CustomNetworkNode outputNode = new CustomNetworkNode();
                    outputNode.setLabel("output");
                    outputNode.setType(NetworkNode.OUTPUT_TYPE);
                    component.getNodes().put(outputNode.getId(),outputNode);
                }

                network.addNode(component);
            }

        }


    }

}
