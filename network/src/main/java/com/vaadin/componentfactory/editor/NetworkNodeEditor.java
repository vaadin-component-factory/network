package com.vaadin.componentfactory.editor;

/*
 * #%L
 * Network Component
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.componentfactory.model.NetworkNode;
import com.vaadin.flow.component.Component;

/**
 * Interface for custom editor for Network node
 * readBean should set the bean (for getBean)
 *
 * @param <TNode> Node type
 * @param <TEdge> Edge type
 */
public interface NetworkNodeEditor<TNode extends NetworkNode<TNode,TEdge>, TEdge> {

    /**
     * Save the node
     *
     * @param node node to be saved
     * @return the saved node
     */
    TNode save(TNode node);

    /**
     * Writes changes to the bean
     *
     * @param node the object to write the field values
     * @return true if there are no errors and the node was updated
     */
    boolean writeBeanIfValid(TNode node);

    /**
     * Read the bean
     *
     * @param node the object to read
     */
    void readBean(TNode node);

    /**
     *
     * @return the main component (Form)
     */
    Component getView();

    /**
     * Should not be null after readBean
     *
     * @return the object
     */
    TNode getBean();
}
