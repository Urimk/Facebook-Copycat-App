package com.example.facebookapp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    public static String authToken = "";

    public static void setAuthToken(String authToken) {
        AuthInterceptor.authToken = authToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        //add jwt token to the header
        Request newRequest  = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + authToken)
                .build();
        return chain.proceed(newRequest);
    }
}
