package com.example.worksafe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MachinistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machinist);

        //BottomAppBar bar = findViewById(R.id.bottomAppBar);
        Button start_button = findViewById(R.id.Start);

        // Definisco il campo ID del worker
        TextView textWorkerID = findViewById(R.id.editTextWorkerID);
        TextView textMachineryID = findViewById(R.id.editTextMachinaryID);

        //Define and attach click listener
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textWorkerID.getText().toString().equals("") || textMachineryID.getText().toString().equals("")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MachinistActivity.this, R.style.DialogeTheme);
                    builder.setTitle("Attenzione!");
                    builder.setMessage("Inserisci tutti i campi.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }else{
                    Intent range = new Intent(MachinistActivity.this, RiskListenActivity.class);
                    range.putExtra("MACHINIST_ID",textWorkerID.getText().toString());
                    range.putExtra("MACHINERY_ID",textMachineryID.getText().toString());
                    startActivity(range);
                }
            }
        });



    }
}
