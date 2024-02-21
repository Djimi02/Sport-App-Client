package com.example.sport_app_client.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private MyAuthManager authManager;

    public TokenInterceptor() {
        authManager = MyAuthManager.getInstance();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //rewrite the request to add bearer token
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer " + authManager.getToken())
                .build();

        return chain.proceed(newRequest);
    }
}
