# Component Factory Network for Vaadin 14+
Network is Java API for [<vcf-network>](https://github.com/vaadin-component-factory/vcf-network) web component for Vaadin 14+ in NPM Mode. 
It provides an easy way to display breadcrumb on web pages.

[Live Demo ↗](https://incubator.app.fi/network-demo/network)

[<img src="https://raw.githubusercontent.com/vaadin-component-factory/network/master/screenshot.png" width="400" alt="Screenshot of network">](https://vaadin.com/directory/components/network)


## Usage
Create instance of `Network`. You can set scale, add/edit/delete nodes/edges.
You can also create component (group of nodes)

```
Network network = new Network();
network.setScale(2);
network.addNode(new NetworkNode("My Node"))
```
Breadcrumbs "Home" and "Components" will be hidden when viewport is less then 420px  


## Setting up for development:
Clone the project in GitHub (or fork it if you plan on contributing)

```
https://github.com/vaadin-component-factory/network
```

To build and install the project into the local repository run 

```mvn install ```

## Demo
To run demo go to `network-demo/` subfolder and run `mvn jetty:run`.
After server startup, you'll be able find demo at [http://localhost:8080/network](http://localhost:8080/network)


# Vaadin Prime
This component is available in Vaadin Prime subscription. It is still open source, but you need to have a valid CVAL license in order to use it. Read more at: https://vaadin.com/pricing

# License & Author
This Add-on is distributed under [Commercial Vaadin Add-on License version 3](http://vaadin.com/license/cval-3) (CVALv3). For license terms, see LICENSE.txt.

Network is written by Vaadin Ltd.
