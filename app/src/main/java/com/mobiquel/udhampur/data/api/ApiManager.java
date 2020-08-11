package com.mobiquel.udhampur.data.api;



import com.mobiquel.udhampur.BuildConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Navjot Singh
 * on 2/3/19.
 */

public class ApiManager {

    private static ApiManager instance;
    private final ApiClient apiClient;


    private ApiManager() {
        apiClient = getRetrofitService();
    }

    public static ApiManager init() {
        if (instance == null) {
            synchronized (ApiManager.class) {
                if (instance == null)
                    instance = new ApiManager();
            }
        }
        return instance;
    }

    public static ApiManager getInstance() {
        return instance;
    }

    private static ApiClient getRetrofitService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                .readTimeout(5000,TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_BASE_URL)
                .build();

        return retrofit.create(ApiClient.class);
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClientBuilder.addInterceptor(logging);

        httpClientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder()
                        .method(request.method(), request.body());
                Response response = chain.proceed(requestBuilder.build());
                return response;
            }
        });

        return httpClientBuilder.build();
    }


    public Call<ResponseBody> registerAppUser(Map<String, String> data) {
        return apiClient.registerAppUser(data);
    }




}