package com.atcnetz.de.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MQTTSettingsActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;
    private static final String TAG = "MQTTSettingsActivity";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String MQTT_Server;
    String MQTT_User;
    String MQTT_PW;
    String User_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_settings);
        Log.d(TAG, "Created.");
        prefs = getSharedPreferences("MQTTSettings", MODE_PRIVATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        initMQTTSettings();

        Intent mqttServiceIntent = new Intent(this, MQTTservice.class);
        mqttServiceIntent.setAction("PUBLISH");
        mqttServiceIntent.putExtra("topic", "example/topic");
        mqttServiceIntent.putExtra("message", "Hello, MQTT World!");
        startService(mqttServiceIntent);
    }

    void initMQTTSettings() {

        MQTT_Server = prefs.getString("MQTT_Server", "tcp://test.mosquitto.org:1883");
        MQTT_User = prefs.getString("MQTT_User", "ATCUser");
        MQTT_PW = prefs.getString("MQTT_PW", "12345678");
        User_Name = prefs.getString("User_Name", "Name");

        EditText text_mqtt_server = findViewById(R.id.text_mqtt_url);
        text_mqtt_server.setText(MQTT_Server);
        EditText text_mqtt_user = findViewById(R.id.text_mqtt_user);
        text_mqtt_user.setText(MQTT_User);
        EditText text_mqtt_pw = findViewById(R.id.text_mqtt_pw);
        text_mqtt_pw.setText(MQTT_PW);
        EditText text_user_name = findViewById(R.id.text_name);
        text_user_name.setText(User_Name);

        Button btn_apply = findViewById(R.id.applyButtonID);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                //editor.putInt("DisplayMovement", 0);
                //editor.apply();
                String server = text_mqtt_server.getText().toString();
                String user = text_mqtt_user.getText().toString();
                String pass = text_mqtt_pw.getText().toString();
                Log.d(TAG, "Testing MQTT:\n\t" + server + "\n\t" + user + "\n\t" + pass + "\n\t" + text_user_name.getText());

                Intent mqttServiceIntent = new Intent(getApplicationContext(), MQTTservice.class);
                mqttServiceIntent.setAction("MQTT_CONNECT");
                mqttServiceIntent.putExtra("MQTT_Server", server);
                mqttServiceIntent.putExtra("MQTT_User", user);
                mqttServiceIntent.putExtra("MQTT_Pass", pass);
                mqttServiceIntent.putExtra("message", "Hello, MQTT World!");
                startService(mqttServiceIntent);
            }
        });

        Button btn_back = findViewById(R.id.backButtonID);
        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mqttServiceIntent = new Intent(getApplicationContext(), MQTTservice.class);
                mqttServiceIntent.setAction("MQTT_DISCONNECT");
                startService(mqttServiceIntent);
            }
        });
    }




}