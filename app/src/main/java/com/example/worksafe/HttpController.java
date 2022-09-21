package com.example.worksafe;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpController {

    private static RetrofitInterface retrofitInterface;
    private static Retrofit retrofit;
    private static String BASE_URL = "http://10.20.11.166:3000";

    public static void start(){
        // Creo l'oggetto Retrofit con il base url e il convertitore JSON
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instanzio l''interfaccia utilizzando l'oggetto appena creato
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }
}
