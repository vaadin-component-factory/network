package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.demo.data.CustomNetworkComponent;
import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.model.AbstractNetworkComponent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route("listener")
public class ListenerTestView extends VerticalLayout {

    private Network<CustomNetworkComponent,CustomNetworkNode, CustomNetworkEdge> network = new Network<>(CustomNetworkComponent.class,CustomNetworkNode.class,CustomNetworkEdge.class);

    public ListenerTestView() {
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
        node.setType("input");
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
        CustomNetworkComponent ncomponent = new CustomNetworkComponent();

        ncomponent.setLabel("Component");
        ncomponent.setX(0);
        ncomponent.setY(0);
        ncomponent.setType("component");


        CustomNetworkNode node4 = new CustomNetworkNode();
        node4.setLabel("Node 4");
        node4.setX(-50);
        node4.setY(0);
        ncomponent.getNodes().add(node4);
        //network.addNode(ncomponent);

        System.out.println("adding Edge");
        for (int i = 1; i < network.getNodes().size(); i++) {
            CustomNetworkEdge networkEdge = new CustomNetworkEdge();
            networkEdge.setFrom(network.getNodes().get(i-1).getId());
            networkEdge.setTo(network.getNodes().get(i).getId());
            network.addEdge(networkEdge);
        }
        network.addNetworkAfterDeleteNodesListener(event -> {
            Notification.show("Nodes deleted =" + event.getNetworkNodesId());

            Notification.show("Find all linked edges and delete it");
            List<CustomNetworkEdge> edges = network.getEdges().stream().filter(customNetworkEdge -> event.getNetworkNodesId().contains(customNetworkEdge.getTo())||event.getNetworkNodesId().contains(customNetworkEdge.getFrom())).collect(Collectors.toList());

            Notification.show(edges.size() +" edges found");
            // delete it
            network.deleteEdges(edges);

        });
/*
        network.retrieveNodes(networkNodes -> {
            System.out.println("adding Edge");
            for (int i = 1; i < networkNodes.size(); i++) {
                NetworkEdge networkEdge = new NetworkEdge();
                networkEdge.setFrom(networkNodes.get(i-1).getId());
                networkEdge.setTo(networkNodes.get(i).getId());
                network.addEdge(networkEdge);
            }
        });
*/
    /*    network.addNetworkNewNodeListener(event -> Notification.show("New node =" + event.getNetworkNode()));
        network.addNetworkDeleteNodesListener(event -> Notification.show("Nodes deleted =" + event.getNetworkNodesId()));
*/
  /*      network.addNetworkDeleteNodesListener(event -> {
            boolean ok = event.getNetworkNodesId().size() <= 2;
            if (ok) {
                Notification.show("Can delete selected nodes ");
            } else {
                Notification.show("Cant delete more than 2 nodes");
            }
            return ok;

        });*/
/*
        network.addNetworkAfterDeleteEdgesListener(event -> {
            Notification.show("Edges deleted =" + event.getNetworkEdgesId());
        });*/

/*
        network.addNetworkAfterDeleteNodesListener(event -> {
            Notification.show("Nodes deleted =" + event.getNetworkNodesId());
        });
*/
      /*  network.addNetworkAfterNewEdgesListener(event -> {
            Notification.show("Edges added =" + event.getNetworkEdges());
        });

        network.addNetworkAfterNewNodesListener(event -> {
            Notification.show("addNetworkAfterNewNodesListener Added =" + event.getNetworkNodes());
        });

        network.addNetworkNewEdgesListener(event -> {
            Notification.show("Before new edge");
            boolean nok = true;
            if (event.getNetworkEdges().size() == 1) {
                NetworkEdge edge = event.getNetworkEdges().get(0);
                // check if there is an edge from <- to
                nok = network.getEdges().stream()
                        .filter(networkEdge ->
                            edge.getFrom().equals(networkEdge.getTo())
                        ).anyMatch(networkEdge ->
                                edge.getTo().equals(networkEdge.getFrom())
                        );
                if (nok){
                    Notification.show("Cannot add an edge because there is an edge in the opposite direction");
                }
            }
            return !nok;
        });
*/

        network.addNetworkAfterNewNodesListener(event -> {
            Notification.show("addNetworkAfterNewNodesListener Added =" + event.getNetworkNodes());
        });

      network.addNewEdgesListener(event -> {
          boolean nok = true;
          if (event.getNetworkEdges().size() == 1) {
              NetworkEdge edge = event.getNetworkEdges().get(0);
              // check if there is an edge from <- to
              nok = network.getEdges().stream()
                      .filter(networkEdge ->
                              edge.getFrom().equals(networkEdge.getTo())
                      ).anyMatch(networkEdge ->
                              edge.getTo().equals(networkEdge.getFrom())
                      );
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

        network.addNewComponentListener(e ->
                Notification.show("New component called"));

        network.addNetworkSelectionListener(event -> {
           Notification.show("Nodes selected "+ event.getNetworkNodes());
           Notification.show("Edges selected "+ event.getNetworkEdges());
        });

        Button addEdgesButton = new Button("add edges", e ->{
                System.out.println("adding Edge");
        for (int i = 1; i < network.getNodes().size(); i++) {
            CustomNetworkEdge networkEdge = new CustomNetworkEdge();
            networkEdge.setFrom(network.getNodes().get(i-1).getId());
            networkEdge.setTo(network.getNodes().get(i).getId());
            network.addEdge(networkEdge);
        }});
        add(addEdgesButton);

        Button checkAllButton = new Button("check all", e -> {
            System.out.println(network.getRootData().toJson().toString());
            Notification.show(" rootdata ");
        });
        Button checkNodesListButton = new Button("check nodes list", e -> {
            System.out.println("node list "+ network.getNodes());
            Notification.show("nodes "+ network.getNodes());
        });

        Button checkEdgesListButton = new Button("check edges list", e -> {
            System.out.println("edge list "+ network.getEdges());
            Notification.show("edges "+ network.getEdges());
        });
        add(checkAllButton,checkNodesListButton, checkEdgesListButton);
       // network.addNetworkDeleteEdgesListener(event -> Notification.show("Edges deleted =" + event.getNetworkEdgesId()));
    }

}
