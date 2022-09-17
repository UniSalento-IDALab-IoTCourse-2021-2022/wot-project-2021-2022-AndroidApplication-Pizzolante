package com.example.worksafe;

import android.os.ParcelUuid;

public class BeaconsResult {

    private String name;
    private String mac;
    private String uuid;

    public BeaconsResult(String name, String mac, String uuid) {
        this.name = name;
        this.mac = mac;
        this.uuid = uuid;
    }

    public BeaconsResult(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getUuid() {
        return uuid;
    }
}
