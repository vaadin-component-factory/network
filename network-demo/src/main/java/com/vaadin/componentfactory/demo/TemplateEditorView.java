package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.model.NetworkEdgeImpl;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.componentfactory.model.NetworkNodeImpl;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("template")
public class TemplateEditorView extends VerticalLayout {

    private VerticalLayout networkContainer = new VerticalLayout();
    private Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);

    private VerticalLayout templateNetworkContainer = new VerticalLayout();


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
    private TextField labelField;
    private Network<NetworkNodeImpl, NetworkEdgeImpl> templateNetwork;

    private void openTemplateEditor(NetworkNodeImpl template) {
      /* Test case for the component in a dialog
         Does not work yet.
        Dialog dialog = new Dialog();
        Network<NetworkNodeImpl, NetworkEdgeImpl> templateNetwork = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);
        dialog.add(templateNetwork);
        dialog.open();*/


        templateNetwork = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class, template);

        templateNetwork.setTemplatePanelVisible(false);
        templateNetwork.setLeftPanelOpened(false);
        templateNetwork.setRightPanelOpened(false);
        Button closeButton = new Button("Save and close", e -> closeTemplateEditor());
        networkContainer.setVisible(false);
        templateNetwork.setSizeFull();
        templateNetworkContainer.setVisible(true);
        templateNetworkContainer.add(new H3("Edit template"));
        labelField = new TextField("labelField");
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
