package com.atcnetz.de.notification;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//import org.eclipse.paho.android.service.MqttAndroidClient;
import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.Arrays;

public class MQTTservice extends Service {

    private static final String TAG = "MQTTservice";
    private PendingIntent data;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public static final int MSG_MQTT_CONNECT = 1;
    public static final int MSG_MQTT_SEND = 2;

    private MqttAndroidClient mqttAndroidClient;
    private final String clientId = "Android_Mqtt".concat(String.valueOf(System.currentTimeMillis()));
    //private final String serverUri = "tcp://test.mosquitto.org:1883"; // Replace with your broker's URI
    private final String logintopic = "nico/atcwatch/hello";
    private final String alerttopic = "nico/atcwatch/msg";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private LocalBroadcastManager localBroadcastManager;
    boolean connected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences("MQTTSettings", MODE_PRIVATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Log.d(TAG, "Service started.");
    }

    private void initializeMqtt(String mqttServer, String mqttUser, String mqttPw) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()){
            disconnectMqtt();
        }
        //mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mqttServer, clientId, Ack.AUTO_ACK);


        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d(TAG, "Message arrived: " + message.toString());
                sendBLEcmd("AT+ALMON=1");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Delivery complete");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MQTTservice.this, "Connected!! <3", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Connected to broker with id " + mqttAndroidClient.getClientId());
                    publishMessage(logintopic, clientId);
                    subscribeToTopic(alerttopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MQTTservice.this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed to connect to broker:");
                    Log.d(TAG, exception.getMessage());
                    Log.d(TAG, Arrays.toString(exception.getStackTrace()));
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void disconnectMqtt(){
        try {
            if (mqttAndroidClient != null){
                if (mqttAndroidClient.isConnected()){
                    Log.d(TAG, "Disconnecting...");
                    mqttAndroidClient.disconnect();
                }else{
                    Log.d(TAG, "Not connected, not disconnecting.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed to topic: " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe to topic");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message) {
        try {
            if (mqttAndroidClient.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(message.getBytes());
                mqttMessage.setQos(1); // Set QoS level if needed (0, 1, or 2)
                mqttAndroidClient.publish(topic, mqttMessage);
                Log.d(TAG, "Message published to topic: " + topic);
            } else {
                Log.d(TAG, "Client is not connected, unable to publish message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error Publishing: " + e.getMessage());
        }
    }

    public void sendBLEcmd(String message) {
        Intent intent = new Intent("MSGtoServiceIntentBLEcmd");
        if (message != null)
            intent.putExtra("MSGtoService", message);
        localBroadcastManager.sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*data = intent.getParcelableExtra("pendingIntent");
        Intent result = new Intent();
        result.putExtra("name", "Nico");
        try {
            data.send(this,200,result);
        } catch (PendingIntent.CanceledException e) {
            throw new RuntimeException(e);
        }*/
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("MQTT_CONNECT")) {
                String server = intent.getStringExtra("MQTT_Server");
                String user = intent.getStringExtra("MQTT_User");
                String pass = intent.getStringExtra("MQTT_Pass");
                initializeMqtt(server, user, pass);
            }else if (intent.getAction().equals("MQTT_DISCONNECT")) {
                if (mqttAndroidClient != null) {
                    if (mqttAndroidClient.isConnected()) {
                        Log.d(TAG, "1Connected");
                    } else {
                        Log.d(TAG, "1Disconnected");
                    }
                    disconnectMqtt();
                }
            }else if (intent.getAction().equals("MQTT_PUBLISH")) {
                String topic = intent.getStringExtra("topic");
                String message = intent.getStringExtra("message");
                if (topic != null && message != null) {
                    publishMessage(topic, message);
                }
            }else if (intent.getAction().equals("MQTT_ALERT")) {
                String alertLevel = intent.getStringExtra("alertLevel");
                if (alertLevel != null) {
                    publishMessage(alerttopic, alertLevel);
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectMqtt();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(MQTTservice.this, "BINDING", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MQTT_SEND:
                    Toast.makeText(MQTTservice.this, "Got message", Toast.LENGTH_SHORT).show();
                    Bundle data = msg.getData();
                    String topic = data.getString("topic");
                    String message = data.getString("message");
                    Log.d(TAG, "Topic: " + data.getString("topic"));
                    Log.d(TAG, "Message: " + data.getString("message"));
                    if (topic != null && message != null) {
                        publishMessage(topic, message);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
