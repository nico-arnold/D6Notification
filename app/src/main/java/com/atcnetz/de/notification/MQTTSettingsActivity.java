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
                Log.d(TAG, "Testing MQTT:\n\t" + text_mqtt_server.getText() + "\n\t" + text_mqtt_user.getText() + "\n\t" + text_mqtt_pw.getText() + "\n\t" + text_user_name.getText());
            }
        });

        Button btn_back = findViewById(R.id.backButtonID);
    }




}