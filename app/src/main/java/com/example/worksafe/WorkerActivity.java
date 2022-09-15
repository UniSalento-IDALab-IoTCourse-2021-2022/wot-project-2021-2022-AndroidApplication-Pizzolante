package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class WorkerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        //BottomAppBar bar = findViewById(R.id.bottomAppBar);
        Button scan_button = findViewById(R.id.Scan);

        //Define and attach click listener
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent range = new Intent(WorkerActivity.this, DeviceScanActivity.class);
                startActivity(range);
            }
        });

    }

}
