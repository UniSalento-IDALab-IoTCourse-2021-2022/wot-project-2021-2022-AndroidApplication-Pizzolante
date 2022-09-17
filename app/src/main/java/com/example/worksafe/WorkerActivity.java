package com.example.worksafe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class WorkerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        //BottomAppBar bar = findViewById(R.id.bottomAppBar);
        Button scan_button = findViewById(R.id.Start);

        // Definisco il campo ID del worker
        TextView textWorkerID = findViewById(R.id.editTextWorkerID);

        //Define and attach click listener
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textWorkerID.getText().toString().equals("")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(WorkerActivity.this, R.style.DialogeTheme);
                    builder.setTitle("Attenzione!");
                    builder.setMessage("Inserisci prima il tuo ID");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }else{
                    Intent range = new Intent(WorkerActivity.this, DeviceScanActivity.class);
                    range.putExtra("WORKER_ID",textWorkerID.getText().toString());
                    startActivity(range);

                }
            }
        });

    }

}
