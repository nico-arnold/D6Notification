package com.atcnetz.de.notification;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MQTT extends Activity {

    private LocalBroadcastManager localBroadcastManager;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    //EditText CustomBLEcmd;
    int VibrationIntens = 0;
    int NotificationMode = 0;
    //int DisplayMode = 0;
    //int MovementDisplay = 0;
    int ContrastDisplay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_settings);
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void sendBLEcmd(String message) {
        Intent intent = new Intent("MSGtoServiceIntentBLEcmd");
        if (message != null)
            intent.putExtra("MSGtoService", message);
        localBroadcastManager.sendBroadcast(intent);
    }
}
