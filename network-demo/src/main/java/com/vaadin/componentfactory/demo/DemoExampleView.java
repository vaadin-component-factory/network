package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.model.NetworkEdgeImpl;
import com.vaadin.componentfactory.model.NetworkNodeImpl;
import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoExampleView extends VerticalLayout {

    private Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class, NetworkEdgeImpl.class);

    // simple controls - scale and reset network
    private Button scaleButton = new Button("get scale", e -> showScaleAction());

    private NumberField scale = new NumberField("Scale");

    private Button setScaleButton = new Button("set scale to ", e -> setScaleAction());

    // node controls
    private Binder<NetworkNodeImpl> binderNode = new Binder<>(NetworkNodeImpl.class);
    private TextField nodeUuid = new TextField("id");
    private TextField nodeName = new TextField("Name");
    private NumberField x = new NumberField("x");
    private NumberField y = new NumberField("y");

    private Button addNodeButton = new Button("Add node", e -> addOrEditNodeAction());
    private Button selectNodeButton = new Button("Select node", e -> selectNodeAction());

    private Button newNodeButton = new Button("New node", e -> newNodeAction());

    private ComboBox<NetworkNodeImpl> nodeComboBox = new ComboBox<>("selectedNode");

    private Button refreshComboboxButton = new Button("refresh", event -> refreshNodesAction());

    private Button deleteNodeButton = new Button("Delete node", event -> deleteNodeAction());

    // edge controls

    private ComboBox<NetworkEdgeImpl> edgeComboBox = new ComboBox<>("selectedEdge");

    private Button refreshEdgeComboboxButton = new Button("refresh", event -> refreshEdgesAction());

    private Button deleteEdgeButton = new Button("Delete edge", event -> deleteEdgeAction());

    private Button selectAllButton= new Button("select all", event -> selectAllAction());

    private void selectAllAction() {
        network.select(network.getNodes(),network.getEdges());
    }


    public DemoExampleView() {
        network.setWidthFull();
        addAndExpand(network);
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        add(accordion);
        HorizontalLayout scaleLayout = new HorizontalLayout(scaleButton,scale, setScaleButton);
        scaleLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        accordion.add("Scale",scaleLayout);
        HorizontalLayout selectNodeLayout = new HorizontalLayout(nodeComboBox, selectNodeButton, newNodeButton, refreshComboboxButton, selectAllButton);
        selectNodeLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        HorizontalLayout nodeFormLayout = new HorizontalLayout(nodeUuid, nodeName,x,y, addNodeButton, deleteNodeButton);
        nodeFormLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        accordion.add("Nodes",new Div(selectNodeLayout, nodeFormLayout));


        HorizontalLayout selectEdgeLayout = new HorizontalLayout(edgeComboBox,refreshEdgeComboboxButton,deleteEdgeButton);
        selectEdgeLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        accordion.add("Edges ", new Div(selectEdgeLayout));

        // binder for node
        binderNode.forField(nodeName).asRequired().bind("label");
        binderNode.forField(nodeUuid).bind("id");
        binderNode.forField(x).asRequired().bind(NetworkNode::getX,NetworkNode::setX);
        binderNode.forField(y).asRequired().bind(NetworkNode::getY,NetworkNode::setY);
        nodeUuid.setEnabled(false);

        binderNode.setBean(new NetworkNodeImpl());

        nodeComboBox.setItemLabelGenerator(NetworkNode::getLabel);
    }

    private void showScaleAction() {
        Notification.show("scale="+network.getScale(),1000, Notification.Position.BOTTOM_END);
    }


    private void setScaleAction() {
        network.setScale(scale.getValue());
    }


    private void selectNodeAction() {
        if(nodeComboBox.getValue() != null) {
            binderNode.setBean(nodeComboBox.getValue());
            addNodeButton.setText("edit node");
        }
    }

    private void newNodeAction() {
        binderNode.setBean(new NetworkNodeImpl());
        addNodeButton.setText("Add node");
    }

    private void addOrEditNodeAction() {
        if (binderNode.isValid()){
            if (binderNode.getBean().getId() == null || "".equals(binderNode.getBean().getId()) ) {
                network.addNode(binderNode.getBean());
            } else {
                network.updateNode(binderNode.getBean());
            }
        }
    }

    private void deleteNodeAction() {
        network.deleteNode(binderNode.getBean());
    }

    private void refreshNodesAction() {
        nodeComboBox.setItems(network.getNodes());
    }

    private void refreshEdgesAction() {
        edgeComboBox.setItems(network.getEdges());
    }

    private void deleteEdgeAction() {
        if (edgeComboBox.getValue() != null) {
            network.deleteEdge(edgeComboBox.getValue());
        }
    }

}
