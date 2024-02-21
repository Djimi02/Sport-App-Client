package com.example.sport_app_client.retrofit;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;

    public RetrofitService() {
        TokenInterceptor tokenInterceptor = new TokenInterceptor();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(tokenInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://192.168.1.35:8081")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return this.retrofit;
    }

}