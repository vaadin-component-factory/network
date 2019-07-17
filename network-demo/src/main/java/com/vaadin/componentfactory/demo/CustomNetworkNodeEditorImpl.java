package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.demo.data.CustomNetworkEdge;
import com.vaadin.componentfactory.demo.data.CustomNetworkNode;
import com.vaadin.componentfactory.editor.NetworkNodeEditor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


public class CustomNetworkNodeEditorImpl extends FormLayout implements NetworkNodeEditor<CustomNetworkNode, CustomNetworkEdge> {

    private CustomNetworkNode node;
    private Binder<CustomNetworkNode> binderNode = new Binder<>(CustomNetworkNode.class);

    private TextField id = new TextField("id");
    private TextArea label = new TextArea("Name");
    private NumberField x = new NumberField("x");
    private NumberField y = new NumberField("y");
    private TextField customField = new TextField("custom field");

    public CustomNetworkNodeEditorImpl() {
        add(id,label,x,y, customField);
        id.setReadOnly(true);
        binderNode.bindInstanceFields(this);
    }

    @Override
    public CustomNetworkNode save(CustomNetworkNode node) {
        Notification.show("Node saved");
        return node;
    }

    @Override
    public boolean writeBeanIfValid(CustomNetworkNode node) {
        return binderNode.writeBeanIfValid(node);
    }

    @Override
    public void readBean(CustomNetworkNode node){
        this.node = node;
        binderNode.readBean(node);
    }

    @Override
    public Component getView() {
        return this;
    }

    @Override
    public CustomNetworkNode getBean() {
        return node;
    }


}
