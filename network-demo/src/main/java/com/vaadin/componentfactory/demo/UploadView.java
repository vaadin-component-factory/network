package com.vaadin.componentfactory.demo;

import com.vaadin.componentfactory.Network;
import com.vaadin.componentfactory.exception.NetworkException;
import com.vaadin.componentfactory.model.NetworkEdgeImpl;
import com.vaadin.componentfactory.model.NetworkNodeImpl;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import elemental.json.Json;

import java.io.IOException;
import java.io.InputStream;

@Route("upload")
public class UploadView extends VerticalLayout {

    private Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);
    private Upload upload;
    private Button backButton = new Button("Back and upload a new component", e -> backAction());

    public UploadView() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        upload = new Upload(memoryBuffer);
        upload.addSucceededListener(e -> {
            InputStream inputStream = memoryBuffer.getInputStream();

            try {
                network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class, inputStream);
            } catch (NetworkException nex) {
                nex.printStackTrace();
                Notification.show("Cannot import this file " +nex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
                Notification.show("Cannot use this file");
            }

            addAndExpand(network);
            add(backButton);
            network.setWidthFull();
            // Hide panel for reusable components
            network.setTemplatePanelVisible(false);
            upload.setVisible(false);
        });
        add(upload);

        setSizeFull();
    }

    private void backAction() {
        remove(network);
        remove(backButton);
        upload.setVisible(true);
        // clear upload
        upload.getElement().setPropertyJson("files", Json.createArray());

    }
}
