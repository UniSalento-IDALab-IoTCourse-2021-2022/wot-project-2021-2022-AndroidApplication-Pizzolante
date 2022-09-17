package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

public class RiskListenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_listen);

        // Definisco il topic
        String topic = "worksafe/risks";

        // Creo il client MQTT
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://test.mosquitto.org",
                clientId);

        // Mi connetto e mi sottoscrivo
        MqttMachinistConnect(client,topic);

        // Definisco il pulsante stop
        Button stop_scan_button = findViewById(R.id.stopButton);

        // Definisco e aggancio l'Action Listener al pulsante stopScanButton
        stop_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tolgo la sottoscrizione
                MqttUnsubscribe(client,topic);
                // Mi disconnetto
                MqttDisconnect(client);
                // Torno alla schermata precedente
                Intent previous = new Intent(RiskListenActivity.this, MachinistActivity.class);
                startActivity(previous);
            }
        });

    }

    // Metodo che effettua la connessione al brocker MQTT
    public void MqttMachinistConnect(MqttAndroidClient client, String topic) {

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(RiskListenActivity.this, "Connected to "+client.getServerURI(),
                            Toast.LENGTH_LONG).show();
                    // Una volta connesso correttamente, mi sottoscrivo
                    MqttSubscribe(client,topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(RiskListenActivity.this, "CONNECTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo che sottoscrive il client al topic /worksafe/risks e imposta una callback
    public void MqttSubscribe(MqttAndroidClient client, String topic) {

        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(RiskListenActivity.this, "Correctly subscribed to "+topic,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(RiskListenActivity.this, "SUBSCRIPTION FAILED",
                            Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //Definisco la callback sul client
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(RiskListenActivity.this, "Message arrived to topic "+topic,
                        Toast.LENGTH_LONG).show();
                /*
                    TODO: 1- Estraggo il messaggio
                          2- Controllo l'ID del macchinario: se è associato a me:
                                2.1- Notifica
                                2.2- Creo l'oggetto RiskResult
                                2.3- POST per salvataggio nel db
                 */


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    // Metodo che effettua la disconnessione al brocker MQTT
    public void MqttDisconnect(MqttAndroidClient client) {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(RiskListenActivity.this, "Disconnected from "+client.getServerURI(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(RiskListenActivity.this, "DISCONNECTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo che elimina la sottoscrizione al topic /worksafe/risks
    public void MqttUnsubscribe(MqttAndroidClient client, String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(RiskListenActivity.this, "Correctly unsubscribed to "+topic,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(RiskListenActivity.this, "UNSUBSCRIPTION FAILED",
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

}
