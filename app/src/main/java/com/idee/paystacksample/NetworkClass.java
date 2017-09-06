package com.idee.paystacksample;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by idee on 9/4/17.
 */

public class NetworkClass {

    interface RetrofitRequestClient {
        @POST("/transaction/initialize")
        Call<ResponseBody> fetchAccessCode(@Header("Authorization") String authorization,@Body RequestBody object);
    }

    private static OkHttpClient providesOkHttpClientBuilder(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        return httpClient.readTimeout(1200, TimeUnit.SECONDS)
                .connectTimeout(1200, TimeUnit.SECONDS).build();

    }

    static RetrofitRequestClient providesRetrofitRequestClient () {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://api.paystack.co/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder
                .client(providesOkHttpClientBuilder())
                .build();

        return retrofit.create(RetrofitRequestClient.class);

    }

}
