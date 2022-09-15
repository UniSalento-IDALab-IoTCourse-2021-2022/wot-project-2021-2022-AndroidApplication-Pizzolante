package com.example.worksafe;

public class SettingsResult {

    private int reference_points; // Numero di RP
    private double security_distance; // Distanza di sicurezza
    private int tx_power; // Valore di RSSI (dbm) alla distanza di 1m (Relativo al beacon)
    private int [][] rssi_values; // Valori di RSSI acquisiti durantre la calibrazione
    private double [][] distances; // Distanze tra RP e BS

    public int[][] getRssi_values() {
        return rssi_values;
    }

    public double[][] getDistances() {
        return distances;
    }

    public int getReference_points() {
        return reference_points;
    }

    public double getSecurity_distance() {
        return security_distance;
    }

    public int getTx_power() {
        return tx_power;
    }


}
