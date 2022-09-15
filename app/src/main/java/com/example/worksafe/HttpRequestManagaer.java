package com.example.worksafe;

import android.app.AlertDialog;
import android.content.Context;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestManagaer {

    private static HttpRequestManagaer single_instance = null;

    public Retrofit retrofit;
    public RetrofitInterface retrofitInterface;
    public String BASE_URL = "http://192.168.43.237:3000";
    public List<SettingsResult> settings;
    public List<BeaconsResult> beacons;

    // Static method
    // Static method to create instance of Singleton class
    public static HttpRequestManagaer getInstance()
    {
        if (single_instance == null)
            single_instance = new HttpRequestManagaer();

        return single_instance;
    }

    public  List<SettingsResult> getSettings(Context context){

        // Creo l'oggetto Retrofit con il base url e il convertitore JSON
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instanzio l''interfaccia utilizzando l'oggetto appena creato
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<List<SettingsResult>> call = retrofitInterface.getSettings();
        // Inserisco la chiamata in una coda
        call.enqueue(new Callback<List<SettingsResult>>() {
            @Override
            public void onResponse(Call<List<SettingsResult>> call, Response<List<SettingsResult>> response) {
                // Riporto il codice di errore se la chiamata non va a buon fine
                if (!response.isSuccessful()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogeTheme);
                    builder.setTitle("Ops!");
                    builder.setMessage(response.code());
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    settings = null;
                }
                // La chiamata ha avuto successo...
                // Creo una lista di oggetti SettingResult e prendo la risposta dal server
                settings = response.body();
            }


            @Override
            public void onFailure(Call<List<SettingsResult>> call, Throwable t) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogeTheme);
                builder.setTitle("Ops!");
                builder.setMessage(t.getMessage());
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
                settings = null;
            }
        });
        return settings;
    }

    public List<BeaconsResult> getDevices(Context context){

        // Creo l'oggetto Retrofit con il base url e il convertitore JSON
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Instanzio l''interfaccia utilizzando l'oggetto appena creato
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<List<BeaconsResult>> call = retrofitInterface.getBeacons();
        // Inserisco la chiamata in una coda
        call.enqueue(new Callback<List<BeaconsResult>>() {
            @Override
            public void onResponse(Call<List<BeaconsResult>> call, Response<List<BeaconsResult>> response) {
                beacons = new ArrayList<>();
                // Riporto il codice di errore se la chiamata non va a buon fine
                if (!response.isSuccessful()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogeTheme);
                    builder.setTitle("Attenzione!");
                    builder.setMessage(response.code());
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    return;
                }
                // La chiamata ha avuto successo...
                // Creo una lista di oggetti SettingResult e prendo la risposta dal server
                beacons = response.body();
            }


            @Override
            public void onFailure(Call<List<BeaconsResult>> call, Throwable t) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogeTheme);
                builder.setTitle("Attenzione!");
                builder.setMessage(t.getMessage());
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
                beacons = null;
            }
        });
        return beacons;
    }

}
