package com.adedom.basicandroid.util;

import com.adedom.basicandroid.ConnectDB;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(ConnectDB.BASE_IMAGE).
                    addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
