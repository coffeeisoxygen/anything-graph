package com.coffeecode.ui;

import com.coffeecode.model.LocationNode;

public interface NodeChangeListener {

    void onNodeAdded(LocationNode node);

    void onNodeRemoved(LocationNode node);
}
