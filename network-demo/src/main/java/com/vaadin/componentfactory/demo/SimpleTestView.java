package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.model.NetworkComponent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("simple")
public class SimpleTestView extends VerticalLayout {

    private Network<NetworkComponent,NetworkNode,NetworkEdge> network = new Network<>(NetworkComponent.class,NetworkNode.class,NetworkEdge.class);

    public SimpleTestView() {
        Button checkNodesListButton = new Button("check nodes list", e -> {
            System.out.println("node list "+ network.getNodes());
            Notification.show("nodes "+ network.getNodes());
        });

        Button checkEdgesListButton = new Button("check edges list", e -> {
            System.out.println("edge list "+ network.getEdges());
            Notification.show("eges "+ network.getEdges());
        });
        add(network, checkNodesListButton, checkEdgesListButton);
    }

}
