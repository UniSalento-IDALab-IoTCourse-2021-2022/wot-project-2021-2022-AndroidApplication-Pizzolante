package com.example.worksafe;

public class DangerResult {

    private String workerId;
    private String beaconId; // Nome del dispositivo
    private String message;
    private String timestamp;

    public DangerResult(String workerId, String beaconId, String message, String timestamp) {
        this.workerId = workerId;
        this.beaconId = beaconId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }


}
