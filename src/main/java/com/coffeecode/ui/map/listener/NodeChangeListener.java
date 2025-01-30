package com.coffeecode.ui.map.listener;

import com.coffeecode.model.LocationNode;

public interface NodeChangeListener {

    void onNodeAdded(LocationNode node);

    void onNodeRemoved(LocationNode node);

    void onStartNodeChanged(LocationNode node);

    void onEndNodeChanged(LocationNode node);
}
