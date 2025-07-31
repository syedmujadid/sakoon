package com.mobiquel.udhampur.base.dummy;

import com.mobiquel.udhampur.base.BasePresenter;

public class DummyPresenter extends BasePresenter<DummyView> implements DummyModelListener {

    private DummyModel model;

    public DummyPresenter(DummyView view) {
        super(view);
    }

    @Override
    protected void setModel() {
        model = new DummyModel(this);
    }

    @Override
    protected void destroy() {
        if (model != null)
            model.detachListener();
        model = null;
    }

    @Override
    protected void initView() {

    }

}
