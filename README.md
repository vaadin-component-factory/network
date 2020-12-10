# Component Factory Network for Vaadin 14+
Network is Java API for [<vcf-network>](https://github.com/vaadin-component-factory/vcf-network) web component for Vaadin 14+ in NPM Mode. 
It provides an easy way to display network on web pages.

You can read the web component documentation to know how to use it: [Documentation of <vcf-network>](https://vaadin.com/directory/component/vaadin-component-factoryvcf-network)

[Live Demo ↗](https://incubator.app.fi/network-demo/network)

[<img src="https://raw.githubusercontent.com/vaadin-component-factory/network/master/screenshot.png" alt="Screenshot of network">](https://vaadin.com/directory/components/network-component)


## Usage
Create instance of `Network`. You can set scale, add/edit/delete nodes/edges.
You can also create component (group of nodes and edges).
You can navigate to a component when double-click. 

```
Network network = new Network();
network.setScale(2);
network.addNode(new NetworkNode("My Node"))
```

### Listeners

You can call Java function on events:
* on-select (nodes,edges)
* on-new-node (nodes to add)
* after-new-node (added nodes)
* on-update-node (nodes to update)
* on-delete-node (id of nodes to delete)
* after-delete-node (id of deleted nodes)
* on-hover-node (node hovered)
* on-new-edge (edges to add)
* after-new-edge (added edges)
* on-update-edge (edges to update)
* on-delete-edge (id of edges to delete)
* after-delete-edge (id of deleted edges)
* on-hover-edge (edge hovered)
* on-create-component (id of selected nodes)

Example:
```
Network<NetworkNodeImpl, NetworkEdgeImpl> network = new Network<>(NetworkNodeImpl.class,NetworkEdgeImpl.class);
network.setWidthFull();
addAndExpand(network);

network.addNetworkSelectionListener(event -> {
    Notification.show("Nodes selected "+ event.getNetworkNodes());
    Notification.show("Edges selected "+ event.getNetworkEdges());
});
```

[<img src="https://raw.githubusercontent.com/vaadin-component-factory/network/master/selection-listener-screenshot.png" alt="Screenshot of network with a listener">](https://vaadin.com/directory/components/network-component)

## Custom editor for Node

You can customize node properties and create your own editor in Java (See CustomEditorView):

```
private Network<CustomNetworkNode, CustomNetworkEdge> network = new Network<>(CustomNetworkNode.class,CustomNetworkEdge.class);
network.setWidthFull();
addAndExpand(network);
network.addNodeEditor(new CustomNetworkNodeEditorImpl());
```

[<img src="https://raw.githubusercontent.com/vaadin-component-factory/network/master/custom-editor-screenshot.png" alt="Screenshot of network with a custom editor">](https://vaadin.com/directory/components/network-component)


## Setting up for development:
Clone the project in GitHub (or fork it if you plan on contributing)

```
https://github.com/vaadin-component-factory/network
```

To build and install the project into the local repository run 

```mvn install ```

## Demo
To run demo go to `network-demo/` subfolder and run `mvn jetty:run`.
After server startup, you'll be able find demo at [http://localhost:8080/](http://localhost:8080/)

You can also test:
 * the template editor here: [http://localhost:8080/template](http://localhost:8080/template)
 * Import functionality here: [http://localhost:8080/upload](http://localhost:8080/upload)

## Known limitations

* This component does not work in a vaadin dialog.
* This component does not work on IE11 and Edge.



## License & Author

This Add-on is distributed under Apache 2.0

Component Factory Network is written by Vaadin Ltd.

### Sponsored development
Major pieces of development of this add-on has been sponsored by multiple customers of Vaadin. Read more  about Expert on Demand at: [Support](https://vaadin.com/support) and  [Pricing](https://vaadin.com/pricing)


