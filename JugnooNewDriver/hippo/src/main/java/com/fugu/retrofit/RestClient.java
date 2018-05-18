package com.fugu.retrofit;


import com.fugu.database.CommonData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Rest Client
 */
public class RestClient {

    public static Retrofit retrofit = null;


    /**
     * @return
     */
    public static ApiInterface getApiInterface() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CommonData.getServerUrl())
                    //.baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient().build())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }


    /**
     * @return
     */
    public static Retrofit getRetrofitBuilder() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CommonData.getServerUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient().build())
                    .build();
        }
        return retrofit;
    }

    /**
     * @return
     */
    private static OkHttpClient.Builder httpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        //logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);

        // add_small your other interceptors …
        // add_small logging as last interceptor
        httpClient.addInterceptor(logging);
        return httpClient;
    }

}
