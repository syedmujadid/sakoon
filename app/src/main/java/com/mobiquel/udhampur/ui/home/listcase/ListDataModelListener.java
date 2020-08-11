package com.mobiquel.udhampur.ui.home.listcase;

import com.mobiquel.udhampur.base.BaseModelListener;

import org.json.JSONObject;

public interface ListDataModelListener extends BaseModelListener {
    void handleListResult(JSONObject object);
    void handleFailureResponse();
}
