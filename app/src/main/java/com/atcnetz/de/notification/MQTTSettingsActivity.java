package com.atcnetz.de.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MQTTSettingsActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int DisplayMode = 0;
    int MovementDisplay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_settings);
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }


    public void sendBLEcmd(String message) {
        Intent intent = new Intent("MSGtoServiceIntentBLEcmd");
        if (message != null)
            intent.putExtra("MSGtoService", message);
        localBroadcastManager.sendBroadcast(intent);
    }



}