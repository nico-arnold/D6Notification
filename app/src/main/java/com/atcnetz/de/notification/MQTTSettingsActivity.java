package com.atcnetz.de.notification;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputFilter;
import android.util.Log;

import android.os.Message;
import android.os.Messenger;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MQTTSettingsActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;
    Messenger mMessenger = null;
    // Flag indicating whether we have called bind on the service.
    boolean mBound;

    private static final String TAG = "MQTTSettingsActivity";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String MQTT_Server;
    String MQTT_User;
    String MQTT_PW;
    String User_Name;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            // This is called when the connection with the iBinder has been established, giving us the object we can use
            // to interact with the iBinder.  We are communicating with the iBinder using a Messenger, so here we get a
            // client-side representation of that from the raw IBinder object.
            mMessenger = new Messenger(iBinder);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected -- that is,
            // its process crashed.
            mMessenger = null;
            mBound = false;
        }
    };

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
        //startService(mqttServiceIntent);
        bindService(mqttServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    void initMQTTSettings() {

        MQTT_Server = prefs.getString("MQTT_Server", "tcp://test.mosquitto.org:1883");
        MQTT_User = prefs.getString("MQTT_User", "ATCUser");
        MQTT_PW = prefs.getString("MQTT_PW", "12345678");
        User_Name = prefs.getString("User_Name", "Name");

        editor = prefs.edit();
        editor.putString("Foo", "Bar");
        editor.apply();

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
                Log.d(TAG, "Clicked on Apply...");
                String server = text_mqtt_server.getText().toString();
                String user = text_mqtt_user.getText().toString();
                String pass = text_mqtt_pw.getText().toString();
                Log.d(TAG, "Testing MQTT:\n\t" + server + "\n\t" + user + "\n\t" + pass + "\n\t" + text_user_name.getText());

                PendingIntent pendingResult = createPendingResult(100, new Intent(), 0);
                //PendingIntent pendingResult = PendingIntent.getActivity(this, 100);
                Intent mqttServiceIntent = new Intent(getApplicationContext(), MQTTservice.class);

                //startService(intent);

                //Intent mqttServiceIntent = new Intent(getApplicationContext(), MQTTservice.class);
                mqttServiceIntent.setAction("MQTT_CONNECT");
                mqttServiceIntent.putExtra("pendingIntent", pendingResult);
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
                /*Bundle data = new Bundle();
                data.putString("topic", "nico/atcwatch/hello");
                data.putString("message", "test123");
                Message msg = Message.obtain(null, MQTTservice.MSG_MQTT_SEND, 0, 0);
                msg.setData(data);
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage());
                }*/
                Intent mqttServiceIntent = new Intent(getApplicationContext(), MQTTservice.class);
                mqttServiceIntent.setAction("MQTT_ALERT");
                mqttServiceIntent.putExtra("alertLevel", "1");
                startService(mqttServiceIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode==200) {
            Log.d(TAG, data.getStringExtra("name"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}