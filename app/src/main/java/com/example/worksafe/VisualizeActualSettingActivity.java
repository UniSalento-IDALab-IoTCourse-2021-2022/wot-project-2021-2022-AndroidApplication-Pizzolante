package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class VisualizeActualSettingActivity extends AppCompatActivity {

    private static SettingsResult actualSettings;

    public static SettingsResult getActualSettings() {
        return actualSettings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_settings);

        // Creo la TextView
        TextView textViewResult = findViewById(R.id.text_view_result);
        // Pulsante okay per tornare alla home
        Button ok_button = findViewById(R.id.ok_button);
        // Action listener associato a "ok"
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(VisualizeActualSettingActivity.this,MainActivity.class);
                startActivity(home);
            }
        });

        // Inizializzo il controller HTTP (Retrofit)
        HttpController.start();

        // Creo una chiamata (GET) che ritorna una lista di SettingResult
        Call<List<SettingsResult>> call = HttpController.getRetrofitInterface().getSettings();
        // Inserisco la chiamata in una coda
        call.enqueue(new Callback<List<SettingsResult>>() {
            @Override
            public void onResponse(Call<List<SettingsResult>> call, Response<List<SettingsResult>> response) {

                // Riporto il codice di errore se la chiamata non va a buon fine
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                // La chiamata ha avuto successo...
                // Creo una lista di oggetti SettingResult e prendo la risposta dal server
                List<SettingsResult> settings = response.body();

                actualSettings = settings.get(0);

                // Prendo i dati per la visualizzazione
                String content = "";
                content += "Reference Points (RP): " + actualSettings.getReference_points() + "\n\n";
                content += "Security Distance: " + actualSettings.getSecurity_distance() + " m\n\n";
                content += "TX Power: " + actualSettings.getTx_power() + " dbm\n\n";
                double[][] distances = actualSettings.getDistances();
                content += "Distances [m]:\n";
                int m = distances.length;
                int n = distances[0].length;
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        content += distances[i][j] + "\t";
                    }
                    content += "\n";
                }
                int[][] rssi = actualSettings.getRssi_values();
                content += "\nRSSI values [dbm]:\n";
                m = rssi.length;
                n = rssi[0].length;
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        content += rssi[i][j] + "\t";
                    }
                    content += "\n";
                }
                // Inserisco i dati nella View
                textViewResult.append(content);
            }


            @Override
            public void onFailure(Call<List<SettingsResult>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }
}
