package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.model.NetworkEdgeImpl;
import com.vaadin.componentfactory.model.NetworkNodeImpl;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.demo.Card;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;

@Route("network")
public class NetworkView extends DemoView {

    @Override
    protected void initView() {
        getElement().setAttribute("style","max-width:90%;");
        createSimpleExample();
        createListenerExample();
        createCustomNodeEditorExample();

    }

    private void createSimpleExample() {
        Div message = createMessageDiv("simple-network-message");

        // begin-source-example
        // source-example-heading: Simple network
        Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);
        network.setHeight("600px");
        network.setWidthFull();
        // Hide panel for reusable components
        network.setTemplatePanelVisible(false);
        // end-source-example

        addCard("Simple network", network, message);
    }


    private void createListenerExample() {
        Div message = createMessageDiv("listener-network-message");

        // begin-source-example
        // source-example-heading: Network with listener
        Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);
        network.setHeight("600px");
        network.setWidthFull();
        // Hide panel for reusable components
        network.setTemplatePanelVisible(false);

        // There are more examples in ListenerExampleView
        network.addNetworkSelectionListener(event -> {
            Notification.show("Nodes selected "+ event.getNetworkNodes());
            Notification.show("Edges selected "+ event.getNetworkEdges());
        });

        // end-source-example

        addCard("Network with listener", network, message);
    }


    private void createCustomNodeEditorExample() {
        Div message = createMessageDiv("custom-node-editor-network-message");

        // begin-source-example
        // source-example-heading: Network with custom field for the node
        Network<CustomNetworkNode, CustomNetworkEdge> network = new Network<>(CustomNetworkNode.class,CustomNetworkEdge.class);
        network.setHeight("600px");
        network.setWidthFull();
        // Hide panel for reusable components
        network.setTemplatePanelVisible(false);

        // You can see the full code in the demo source code
        network.addNodeEditor(new CustomNetworkNodeEditorImpl());

        // end-source-example

        Div description = new Div();
        description.setText("Create a select a node to see the custom editor");
        addCard("Network with custom field for the node", network, description, message);
    }





    private Div createMessageDiv(String id) {
        Div message = new Div();
        message.setId(id);
        message.getStyle().set("whiteSpace", "pre");
        return message;
    }

}
