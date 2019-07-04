package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.model.NetworkComponent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("listener")
public class ListenerTestView extends VerticalLayout {

    private Network network = new Network();

    public ListenerTestView() {
        network.setWidthFull();
        addAndExpand(network);
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // put some data in the graph
        NetworkNode node = new NetworkNode();
        node.setLabel("Node 1");
        node.setX(-150);
        node.setY(-80);
        node.setType("input");
        network.addNode(node);


        NetworkNode node2 = new NetworkNode();
        node2.setLabel("Node 2");
        node2.setX(-50);
        node2.setY(-80);
        network.addNode(node2);


        NetworkNode node3 = new NetworkNode();
        node3.setLabel("Node 3");
        node3.setX(-50);
        node3.setY(0);
        network.addNode(node3);
        NetworkComponent ncomponent = new NetworkComponent();

        ncomponent.setLabel("Component");
        ncomponent.setX(0);
        ncomponent.setY(0);
        ncomponent.setType("component");


        NetworkNode node4 = new NetworkNode();
        node4.setLabel("Node 4");
        node4.setX(-50);
        node4.setY(0);
        ncomponent.getNodes().add(node4);
        network.addNode(ncomponent);

        System.out.println("adding Edge");
        for (int i = 1; i < network.getNodes().size(); i++) {
            NetworkEdge networkEdge = new NetworkEdge();
            networkEdge.setFrom(network.getNodes().get(i-1).getId());
            networkEdge.setTo(network.getNodes().get(i).getId());
            network.addEdge(networkEdge);
        }

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
            Notification.show("Nodes Added =" + event.getNetworkNodes());
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
            return true;
        });

        network.addNewComponentListener(e ->
                Notification.show("New component called"));
        Button addEdgesButton = new Button("add edges", e ->{
                System.out.println("adding Edge");
        for (int i = 1; i < network.getNodes().size(); i++) {
            NetworkEdge networkEdge = new NetworkEdge();
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
