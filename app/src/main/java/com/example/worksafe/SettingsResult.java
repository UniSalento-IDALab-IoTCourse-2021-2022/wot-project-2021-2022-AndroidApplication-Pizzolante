package com.example.worksafe;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SettingsResult {

    private int reference_points; // Numero di RP
    private double security_distance; // Distanza di sicurezza
    private int tx_power; // Valore di RSSI (dbm) alla distanza di 1m (Relativo al beacon)
    private ArrayList<int []> rssi_values; // Valori di RSSI acquisiti durantre la calibrazione
    private ArrayList<double []> distances; // Distanze tra RP e BS



}
