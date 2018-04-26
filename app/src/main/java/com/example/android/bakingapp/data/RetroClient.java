package com.example.android.bakingapp.data;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static final String ROOT_URL = "https://s3.cn-north-1.amazonaws.com.cn/";

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static RecipeApiService getApiService() {
        return getRetrofitInstance().create(RecipeApiService.class);
    }
    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
