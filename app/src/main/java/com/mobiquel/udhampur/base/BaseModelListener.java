package com.mobiquel.udhampur.base;


import com.mobiquel.udhampur.pojo.FailureResponse;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public interface BaseModelListener {
    void noNetworkError();

    void onErrorOccurred(FailureResponse failureResponse);

}