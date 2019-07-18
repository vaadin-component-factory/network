package com.vaadin.componentfactory.demo;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode.NodeType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

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
        node.setType(NodeType.INPUT_TYPE);
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
        ncomponent.setType(NodeType.COMPONENT_TYPE);


        CustomNetworkNode node4 = new CustomNetworkNode();
        node4.setLabel("Node 4");
        node4.setX(-50);
        node4.setY(0);
        ncomponent.getNodes().put(node4.getId(),node4);

        CustomNetworkNode nodeInput = new CustomNetworkNode();
        nodeInput.setLabel("My input node");
        nodeInput.setX(0);
        nodeInput.setY(0);
        nodeInput.setType(NodeType.INPUT_TYPE);
        ncomponent.getNodes().put(nodeInput.getId(),nodeInput);

        CustomNetworkNode nodeOutput = new CustomNetworkNode();
        nodeOutput.setLabel("My output node");
        nodeOutput.setX(50);
        nodeOutput.setY(0);
        nodeOutput.setType(NodeType.OUTPUT_TYPE);
        ncomponent.getNodes().put(nodeOutput.getId(),nodeOutput);

        network.addNode(ncomponent);

        CustomNetworkNode previousNode = null;
     /*   for (CustomNetworkNode networkNode : network.getNodes()) {
            if (previousNode != null){
                CustomNetworkEdge networkEdge = new CustomNetworkEdge();
                networkEdge.setFrom(previousNode.getId());
                networkEdge.setTo(networkNode.getId());
                network.addEdge(networkEdge);
            }
            previousNode = networkNode;
        }*/
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
        Button addComponentInCustom = new Button("addCustom", e -> addCustomComponentAction());
        add(new HorizontalLayout(addEdgesButton, checkAllButton,checkNodesListButton, checkEdgesListButton, addComponentInCustom));
    }

    private void addCustomComponentAction() {
        {

            CustomNetworkNode ncomponent = new CustomNetworkNode();

            ncomponent.setLabel("Custom Component");
            ncomponent.setX(0);
            ncomponent.setY(0);
            ncomponent.setType(NodeType.COMPONENT_TYPE);


            CustomNetworkNode node4 = new CustomNetworkNode();
            node4.setLabel("Node 4");
            node4.setX(-50);
            node4.setY(0);
            ncomponent.getNodes().put(node4.getId(),node4);

            CustomNetworkNode nodeInput = new CustomNetworkNode();
            nodeInput.setLabel("My input node");
            nodeInput.setX(0);
            nodeInput.setY(0);
            nodeInput.setType(NodeType.INPUT_TYPE);
            ncomponent.getNodes().put(nodeInput.getId(),nodeInput);

            CustomNetworkNode nodeOutput = new CustomNetworkNode();
            nodeOutput.setLabel("My output node");
            nodeOutput.setX(50);
            nodeOutput.setY(0);
            nodeOutput.setType(NodeType.OUTPUT_TYPE);
            ncomponent.getNodes().put(nodeOutput.getId(),nodeOutput);
            network.addTemplate(ncomponent);
        }
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

}
