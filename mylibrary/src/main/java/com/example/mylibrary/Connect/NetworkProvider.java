package com.example.mylibrary.Connect;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkProvider {
    private static volatile NetworkProvider mInstance = null;
    private String BASE_URL = "http://103.141.140.189:8899/";
    private Retrofit retrofit;
    private NetworkProvider() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient.Builder().build())
               .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
    }
    public static NetworkProvider self() {
        if (mInstance == null)
            mInstance = new NetworkProvider();
        return mInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
