package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomappbar.BottomAppBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BottomAppBar bar = findViewById(R.id.bottomAppBar);
        Button cal_button = findViewById(R.id.CButton);
        Button worker_button = findViewById(R.id.WButton);
        Button machinist_button = findViewById(R.id.MButton);

        //Define and attach click listener
        cal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cal = new Intent(MainActivity.this,CalibrationActivity.class);
                startActivity(cal);
            }
        });

        //Define and attach click listener
        worker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent worker = new Intent(MainActivity.this,WorkerActivity.class);
                startActivity(worker);
            }
        });

        //Define and attach click listener
        machinist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent machinist = new Intent(MainActivity.this,MachinistActivity.class);
                startActivity(machinist);
            }
        });


    }
}