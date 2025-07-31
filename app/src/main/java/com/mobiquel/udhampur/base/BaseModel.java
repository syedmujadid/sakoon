package com.mobiquel.udhampur.base;

import com.mobiquel.udhampur.data.DataManager;
import com.mobiquel.udhampur.network.CommonResponseHandler;

import java.lang.ref.SoftReference;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public abstract class BaseModel<T extends BaseModelListener> implements CommonResponseHandler {

    private static final int NO_NETWORK = 999;
    private SoftReference<T> listener;

    public BaseModel(T listener) {
        this.listener = new SoftReference<>(listener);
    }

    public void attachListener(T listener) {
        this.listener = new SoftReference<>(listener);
    }

    public void detachListener() {
        this.listener = null;
    }

    public T getListener() {
        return (listener != null) ? listener.get() : null;
    }

    public abstract void init();

    public DataManager getDataManager() {
        return DataManager.getInstance();
    }

    @Override
    public void onNetworkError() {
        if (getListener() != null)
            getListener().noNetworkError();
    }

}