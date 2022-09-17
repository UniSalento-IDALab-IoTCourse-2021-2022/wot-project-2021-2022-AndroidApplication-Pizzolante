package com.example.worksafe;

import android.app.*;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.*;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import android.bluetooth.BluetoothAdapter;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

public class DeviceScanActivity extends Activity {

    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private ArrayList<Integer> device_rssi;
    private ArrayList<String> device_mac;
    private ArrayList<String> device_info;
    ArrayAdapter<String> adapter;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.168.43.237:3000";
    private List<ScanFilter> listOfFilters;
    private ArrayList<BeaconsResult> foundDevicesList;
    private SettingsResult actualSetting;
    private RiskResult risk;
    private String workerID;
    private String topic = "worksafe/risks";
    private String clientId = MqttClient.generateClientId();
    private MqttAndroidClient client ;

    //====================================================================================

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        workerID = getIntent().getExtras().getString("WORKER_ID");

        // Definisco il pulsante per stoppare la scansione
        Button stop_scan_button = findViewById(R.id.stopScanButton);

        // Definisco e aggancio l'Action Listener al pulsante stopScanButton
        stop_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previous = new Intent(DeviceScanActivity.this, WorkerActivity.class);
                startActivity(previous);
                stopLeScan();
            }
        });

        // Controllo che i parametri di calibrazione siano stati scaricati
        actualSetting = VisualizeActualSettingActivity.getActualSettings();
        if (actualSetting == null) {
            // Finestra per avvisare l'utente che può mettere l'applicazione in background
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogeTheme);
            builder.setTitle("Ops!");
            builder.setMessage("Scarica prima i parametri.\nVai su Home -> Calibrazione -> Ottieni Parametri.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        } else {
            // Controllo che sia attivo il Bluetooth
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                int REQUEST_ENABLE_BT = 1;
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // Finestra per avvisare l'utente che può mettere l'applicazione in background
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogeTheme);
            builder.setTitle("Okay!");
            builder.setMessage("Mantieni l'applicazione in background. Riceverai una notifica in caso di pericolo\nBuon lavoro!");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();

            //---------------------------------------------------------------------------------------------
            // Chiamata GET per recuperare tutti i dispositivi registrati per permettere il filtraggio durante la scansione

            // Creo l'oggetto Retrofit con il base url e il convertitore JSON
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Instanzio l''interfaccia utilizzando l'oggetto appena creato
            retrofitInterface = retrofit.create(RetrofitInterface.class);
            // Creo una chiamata (GET) che ritorna una lista di SettingResult
            Call<List<BeaconsResult>> call = retrofitInterface.getBeacons();
            // Inserisco la chiamata in una coda
            call.enqueue(new Callback<List<BeaconsResult>>() {
                @Override
                public void onResponse(Call<List<BeaconsResult>> call, Response<List<BeaconsResult>> response) {

                    // La chiamata ha avuto successo...
                    // Creo una lista di oggetti BeaconResult e prendo la risposta dal server
                    List<BeaconsResult> results = response.body();
                    listOfFilters = new ArrayList<>();
                    // Scorro la lista dei beacons dal db e creo la lista dei filtri
                    for (BeaconsResult beacon : results) {
                        ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(beacon.getMac()).build();
                        listOfFilters.add(filter);
                    }
                    // Creo due array: mac e rssi
                    device_mac = new ArrayList<>();
                    device_rssi = new ArrayList<>();
                    // Creo l'arraylist per la visualizzazione dei dispositivi trovati
                    device_info = new ArrayList<>();
                    // Creo l'adapter che per l'aggiornamento della view
                    adapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            device_info);
                    ListView lv = findViewById(R.id.DeviceList);
                    lv.setAdapter(adapter);

                    // Mi connetto al broker mqtt
                    client = new MqttAndroidClient(getApplicationContext(),
                            "tcp://test.mosquitto.org",
                            clientId);
                    MqttWorkerConnect(client,topic);

                    // Inizio la scansione dei device BLE
                    scanLeDevice();
                }

                @Override
                public void onFailure(Call<List<BeaconsResult>> call, Throwable t) {
                    Toast.makeText(DeviceScanActivity.this, t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        //---------------------------------------------------------------------------------------------

    }// OnCreate

    // Callback che viene eseguita ogni volta che un dispositivo viene trovato  (rispettando la lista dei filtri)
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            // Getting the device
            BluetoothDevice device = result.getDevice();
            BeaconsResult foundDevice = new BeaconsResult(device.getName(), device.getAddress(), Arrays.toString(device.getUuids()));
            foundDevicesList.add(foundDevice);

            if (!device_mac.contains(foundDevice.getMac())) {
                device_mac.add(foundDevice.getMac());
                device_rssi.add(result.getRssi());      // Prendo l'RSSI una sola volta
                device_info.add(String.format("Device: " + foundDevice.getName()
                        + "\nMAC:" + foundDevice.getMac()
                        + "\nRSSI: " + result.getRssi() + ""));
                adapter.notifyDataSetChanged();
            }

            //double[] dist= calculateDistances();
            //evaluateDistance(dist);  ====> stopLeScan() if Risk != null
            // Genero il timestamp con la data
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            // Creo il rischio
            RiskResult risk = new RiskResult(workerID,
                        "Attenzione!",
                                 timestamp.toString(),
                                 device.getName());

            MqttPublish(client,topic,risk);
            notifyFoundDevice("Attenzione!");
            //sendRiskResult(risk);
            device_rssi.clear();
            //scanLeDevice();
        }
    };

    // Metodo che fa partire la scansione in background dei dispositivi presenti nella lista di quelli interessati
    private void scanLeDevice() {
        ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)).build();
        List<ScanFilter> filters = listOfFilters; // Make a scan filter matching the beacons I care about
        bluetoothLeScanner.startScan(filters, settings, leScanCallback);
        foundDevicesList = new ArrayList<>();
    }

    // Meotodo che ferma la scansione dei dispositivi BLE
    private void stopLeScan() {
        bluetoothLeScanner.stopScan(leScanCallback);
    }

    // Metodo che serve per inviare una notifica con un messaggio
    private void notifyFoundDevice(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DEVICE_FOUND", "Channel1", importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Create the intent
        Intent intent=new Intent(this,DeviceScanActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        // TODO 2: Impostare l'azione di far ripartire la scansione quando premo ok sulla notifica
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "DEVICE_FOUND")
                .setSmallIcon(R.drawable.worker_worker_icon_with_png_and_vector_format_for_free_481601)
                .addAction(0,"OK",pendingIntent)
                .setContentTitle("Pericolo!")
                .setContentText(message)
                .setPriority(2)
                .setVibrate(new long[]{1000, 1000});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    // Metodo per il calcolo delle distanze utilizzando la tecnica del fingerprint
    private double[] calculateDistances() {
        //Acquisisco tutti i valori necessari... Q,n,m,TX_power... ecc.
        int[][] q = actualSetting.getRssi_values();
        double[][] d = actualSetting.getDistances();
        int m = actualSetting.getReference_points();
        int n = actualSetting.getRssi_values()[0].length;
        int tx_power = actualSetting.getTx_power();
        int xi = 0;
        double[] eculidean_distances = new double[m];
        // Inserisco i valori mancanti in device_rssi
        if (device_rssi.size() < n) {
            xi = n - device_rssi.size();
            for (int i = n - xi; i < n; i++)
                device_rssi.add(-100);
        }

        System.out.println("RSSI VALUES DIM: " + device_rssi.size());
        System.out.println("XI: " + xi);
        //Cerco la zona di riferimento q*
        double localSum = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                localSum += Math.sqrt(Math.pow(device_rssi.get(j), 2) - Math.pow(q[i][j], 2));
            }
            eculidean_distances[i] = localSum;
            localSum = 0;
        }
        // find minimum in eculidean_distances
        int i_star = getMinReference(eculidean_distances);
        int[] q_star = q[i_star];
        double[] distances_c = new double[n];
        //Per ogni valore di p, trovo il valore più vicino in q* --> distanze di calibrazione
        int distance = Math.abs(device_rssi.get(0) - q_star[0]);
        int j_star = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < n; j++) {
                int cdistance = (device_rssi.get(0) - q_star[0]);
                if (cdistance < distance) {
                    j_star = j;
                }
            }
            distances_c[i] = d[i_star][j_star];
        }
        // Dostanze di positioning con fuìormula
        double[] distances_p = new double[n];
        for (int i = 0; i < n; i++) {
            distances_p[i] = Math.pow(10, (tx_power - device_rssi.get(i)) / 20);
        }
        // Calcolo delle distanze finali
        double[] distances_f = new double[n];
        for (int i = 0; i < n; i++) {
            if (Math.abs(distances_c[i] - distances_p[i]) < 1)
                distances_f[i] = distances_p[i];
            else
                distances_f[i] = distances_c[i];
        }
        return distances_f;
    }

    // Meotodo di appoggio che serve a ritornare l'indice in corrispondenza del valore più piccolo in un array
    public static int getMinReference(double[] numbers) {
        int minValue = 0;
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < minValue) {
                minValue = i;
            }
        }
        return minValue;
    }

    // Metodo per la valutazione dela distanza di sicurezza partendo dall'array delle distanze finali
    public RiskResult evaluateDistance(double[] distances) {
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < 5.0) {
                // Genero il messaggio
                String deviceName = foundDevicesList.get(i).getName();
                String message = "Sei a " + distances[i] + " metri da " + deviceName + "!";
                // Genero il timestamp con la data
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                notifyFoundDevice(message);
                // Creo il rischio
                return new RiskResult("", message, timestamp.toString(),deviceName);
            }
        }
        return null;
    }

    public void sendRiskResult(RiskResult riskToPub) {
        // TODO 3: Se i messaggi MQTT arrivano correttamente bisogna inviare il rischio al db con una POST
    }

    // Metodo che effettua la connessione al broker
    public void MqttWorkerConnect(MqttAndroidClient client,String topic){
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DeviceScanActivity.this, "Connected to "+client.getServerURI(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(DeviceScanActivity.this, "CONNECTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo che pubblica il messaggio del client sul topic /worksafe/risks
    public void MqttPublish(MqttAndroidClient client, String topic, RiskResult risk) {
        String payload = risk.getBeaconId();;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}