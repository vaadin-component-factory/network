package com.vaadin.componentfactory.demo.data;

import com.vaadin.componentfactory.model.NetworkEdge;
import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;

public class CustomNetworkEdge extends NetworkEdgeImpl {

    private String customField;

    public String getCustomField() {
        return customField;
    }

    public void setCustomField(String customField) {
        this.customField = customField;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        if (getCustomField() != null) {
            jsonObject.put("custom-field", getCustomField());
        }

        return jsonObject;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        super.readJson(value);

        if (value.hasKey("custom-field")) {
            setCustomField(value.getString("custom-field"));
        }
        return this;
    }

}
