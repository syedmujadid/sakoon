package com.mobiquel.udhampur.data.api;


import com.mobiquel.udhampur.network.NetworkConstants;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

interface ApiClient {



    @POST(NetworkConstants.END_POINT_UPDATE_USER)
    Call<ResponseBody> updateAppUser(@QueryMap Map<String, String> param);


    @POST(NetworkConstants.END_POINT_ADD_USER)
    Call<ResponseBody> registerAppUser(@QueryMap Map<String, String> param);

}