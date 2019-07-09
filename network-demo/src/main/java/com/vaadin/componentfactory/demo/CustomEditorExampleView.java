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

@Route("editor")
public class CustomEditorExampleView extends VerticalLayout {

    private Network<CustomNetworkNode, CustomNetworkEdge> network = new Network<>(CustomNetworkNode.class,CustomNetworkEdge.class);

    public CustomEditorExampleView() {
        network.setWidthFull();
        addAndExpand(network);
        network.addNodeEditor(new CustomNetworkNodeEditorImpl());
    }
}
