package com.vaadin.componentfactory.editor;

/*
 * #%L
 * Network Component
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
