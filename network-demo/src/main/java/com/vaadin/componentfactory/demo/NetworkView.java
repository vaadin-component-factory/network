package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.model.NetworkEdgeImpl;
import com.vaadin.componentfactory.model.NetworkNodeImpl;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.router.Route;

@Route("")
public class NetworkView extends DemoView {

    @Override
    protected void initView() {
        getElement().setAttribute("style","max-width:90%;");
        createSimpleExample();
        createListenerExample();
        createCustomNodeEditorExample();

        createTemplateEditorExample();

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



    private void createTemplateEditorExample() {
        Div message = createMessageDiv("template-editor-network-message");
        Div description = new Div();
        description.setText("Create a new template then edit it");
        addCard("Network with template editor", new TemplateEditorView(), description, message);
    }


    @Route("template")
    // begin-source-example
    // source-example-heading: Network with template editor
    public static class TemplateEditorView extends VerticalLayout {

        // Primary Network component
        private VerticalLayout networkContainer = new VerticalLayout();
        private Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);

        // Network component for editing the template
        private VerticalLayout templateNetworkContainer = new VerticalLayout();
        private TextField labelField;
        private Network<NetworkNodeImpl, NetworkEdgeImpl> templateNetwork;

        public TemplateEditorView() {
            setPadding(false);
            setSpacing(false);
            setSizeFull();
            network.setWidthFull();
            network.addNetworkUpdateTemplateListener(event -> openTemplateEditor(event.getTemplate()));
            networkContainer.setPadding(false);
            networkContainer.setSpacing(false);
            networkContainer.add(new H3("Edit Network"));
            networkContainer.addAndExpand(network);

            templateNetworkContainer.setPadding(false);
            templateNetworkContainer.setSpacing(false);
            add(networkContainer, templateNetworkContainer);

        }

        private void openTemplateEditor(NetworkNodeImpl template) {
            templateNetwork = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class, template);

            templateNetwork.setTemplatePanelVisible(false);
            templateNetwork.setLeftPanelOpened(false);
            templateNetwork.setRightPanelOpened(false);
            Button closeButton = new Button("Save and close", e -> closeTemplateEditor());
            networkContainer.setVisible(false);
            templateNetwork.setSizeFull();
            templateNetworkContainer.setVisible(true);
            templateNetworkContainer.add(new H3("Edit template"));
            labelField = new TextField("Name of your template");
            templateNetworkContainer.addAndExpand(labelField,templateNetwork);
            templateNetworkContainer.add(closeButton);
            // bind
            labelField.setValue(template.getLabel());
        }

        private void closeTemplateEditor() {
            NetworkNodeImpl template = templateNetwork.getRootData();
            template.setLabel(labelField.getValue());
            network.updateTemplate(template);
            templateNetworkContainer.removeAll();
            networkContainer.setVisible(true);
            templateNetworkContainer.setVisible(false);
        }

    }
    // end-source-example


    private Div createMessageDiv(String id) {
        Div message = new Div();
        message.setId(id);
        message.getStyle().set("whiteSpace", "pre");
        return message;
    }
}
