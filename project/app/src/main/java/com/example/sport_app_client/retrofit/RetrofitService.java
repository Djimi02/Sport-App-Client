package com.example.sport_app_client.retrofit;

import com.example.sport_app_client.model.member.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Member.class, new MemberDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://192.168.1.35:8081")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return this.retrofit;
    }

}