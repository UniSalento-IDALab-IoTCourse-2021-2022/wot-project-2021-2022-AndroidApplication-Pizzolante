package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CalibrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);


        Button visualize_setting_button = findViewById(R.id.VisSettings);
        //Button new_calibration_button = findViewById(R.id.StartCalibration);

        //Define and attach click listener
        visualize_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vis = new Intent(CalibrationActivity.this, VisualizeActualSettingActivity.class);
                startActivity(vis);
            }
        });

        //Define and attach click listener
        visualize_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vis = new Intent(CalibrationActivity.this, VisualizeActualSettingActivity.class);
                startActivity(vis);
            }
        });
    }
}

