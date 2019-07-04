package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.model.NetworkComponent;
import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;

@Route("network")
public class NetworkView extends DemoView {

    @Override
    protected void initView() {
        getElement().setAttribute("style","max-width:90%;");
        Network<NetworkComponent,NetworkNode, NetworkEdge> network = new Network<>(NetworkComponent.class,NetworkNode.class,NetworkEdge.class);
        Div description = new Div();
        description.setText("Basic usage");
        addCard("Basic Network usage", network, description);

        Div descriptionJava = new Div();
        descriptionJava.setText("You can create/edit/delete nodes and edges.");
        addCard("Network usage from JAVA", new DemoTestView(), descriptionJava);


        Div descriptionEvent = new Div();
        descriptionEvent.setText("You can listen events (new/delete) for nodes and edges.");
        addCard("Events received from JAVA", new ListenerTestView(), descriptionEvent);


    }
}
