package com.mobiquel.udhampur.network;

import android.support.annotation.NonNull;


import com.mobiquel.udhampur.pojo.FailureResponse;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class NetworkResponse<T> implements Callback<T> {
    private CommonResponseHandler handler;

    public NetworkResponse(CommonResponseHandler handler) {
        this.handler = handler;
    }

    public abstract void onSuccess(T body);

    public abstract void onFailure(int code, FailureResponse failureResponse);

    public abstract void onError(Throwable t);


    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.body() != null && response.isSuccessful()) {
            onSuccess(response.body());

        } else {
            onFailure(response.code(), getFailureErrorBody(response));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (t instanceof SocketTimeoutException || t instanceof UnknownHostException) {
            if (handler != null)
                handler.onNetworkError();
        }
        onError(t);
    }


    /**
     * Create your custom failure response out of server response
     */
    private FailureResponse getFailureErrorBody(Response<T> errorBody) {
        FailureResponse baseResponse = new FailureResponse();
        baseResponse.setMessage(errorBody.message());
        baseResponse.setCode(errorBody.code());
        return baseResponse;
    }

}
