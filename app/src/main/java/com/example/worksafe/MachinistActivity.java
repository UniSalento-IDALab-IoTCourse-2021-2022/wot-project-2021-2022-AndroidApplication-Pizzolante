package com.example.worksafe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class MachinistActivity extends AppCompatActivity{

    private Spinner spinnerMachinaryIDs;
    private final List<String>  machinaryDevices  = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machinist);

        //BottomAppBar bar = findViewById(R.id.bottomAppBar);
        Button start_button = findViewById(R.id.Start);

        // Definisco il campo ID del worker
        TextView textWorkerID = findViewById(R.id.textView2);

        //======================================================================================
        // GET per recuperare la lista di tutti i dispositivi presenti nel database
        // Inizializzo il controller HTTP (Retrofit)
        HttpController.start();
        // Creo una chiamata (GET) che ritorna una lista di SettingResult
        Call<List<BeaconsResult>> call = HttpController.getRetrofitInterface().getBeacons();
        // Inserisco la chiamata in una coda
        call.enqueue(new Callback<List<BeaconsResult>>() {
            @Override
            public void onResponse(Call<List<BeaconsResult>> call, Response<List<BeaconsResult>> response) {

                // La chiamata ha avuto successo...
                // Creo una lista di oggetti BeaconResult e prendo la risposta dal server
                List<BeaconsResult> results = response.body();
                // Scorro la lista dei beacons dal db e creo la lista dei filtri
                for (BeaconsResult beacon : results) {
                    machinaryDevices.add(beacon.getName());
                }
                // Definisco lo spinner e le sue proprietà
                spinnerMachinaryIDs = (Spinner)findViewById(R.id.spinner);
                //Creating the ArrayAdapter instance having the country list
                ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,machinaryDevices);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spinnerMachinaryIDs.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<BeaconsResult>> call, Throwable t) {
                Toast.makeText(MachinistActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        //======================================================================================

        //Define and attach click listener
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textWorkerID.getText().toString().equals("") || spinnerMachinaryIDs.getSelectedItem().toString().equals("")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MachinistActivity.this, R.style.DialogeTheme);
                    builder.setTitle("Attenzione!");
                    builder.setMessage("Inserisci tutti i campi.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }else{
                    Intent range = new Intent(MachinistActivity.this, DangerListenActivity.class);
                    range.putExtra("MACHINIST_ID",textWorkerID.getText().toString());
                    range.putExtra("MACHINERY_ID",spinnerMachinaryIDs.getSelectedItem().toString());
                    startActivity(range);
                }
            }
        });
    }// On create

}
