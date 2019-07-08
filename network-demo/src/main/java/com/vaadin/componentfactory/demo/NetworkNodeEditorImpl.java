package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.demo.data.NetworkEdgeImpl;
import com.vaadin.componentfactory.demo.data.NetworkNodeImpl;
import com.vaadin.componentfactory.editor.NetworkNodeEditor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


public class NetworkNodeEditorImpl extends FormLayout implements NetworkNodeEditor<NetworkNodeImpl, NetworkEdgeImpl> {

    private NetworkNodeImpl node;
    private Binder<NetworkNodeImpl> binderNode = new Binder<>(NetworkNodeImpl.class);

    private TextField id = new TextField("id");
    private TextField label = new TextField("Name");
    private NumberField x = new NumberField("x");
    private NumberField y = new NumberField("y");

    public NetworkNodeEditorImpl() {
        add(id,label,x,y);
        binderNode.forField(label).withValidator(l -> l.length()> 3, "Label should have more then 3 chars ").bind("label");
        binderNode.bindInstanceFields(this);

    }

    @Override
    public NetworkNodeImpl save(NetworkNodeImpl node) {
        Notification.show("Node saved");
        return node;
    }

    @Override
    public boolean writeBeanIfValid(NetworkNodeImpl node) {
        return binderNode.writeBeanIfValid(node);
    }

    @Override
    public void readBean(NetworkNodeImpl node){
        this.node = node;
        binderNode.readBean(node);
    }

    @Override
    public Component getView() {
        return this;
    }

    @Override
    public NetworkNodeImpl getBean() {
        return node;
    }


}
