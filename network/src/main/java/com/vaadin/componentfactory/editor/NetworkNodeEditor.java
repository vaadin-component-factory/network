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

public interface NetworkNodeEditor<TNode extends NetworkNode<TNode,TEdge>, TEdge> {

    TNode save(TNode node);

    boolean writeBeanIfValid(TNode node);

    void readBean(TNode node);

    Component getView();

    TNode getBean();
}
