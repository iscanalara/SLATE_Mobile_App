package com.alaraiscan.slate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.alaraiscan.slate.BluetoothService.LocalBinder;

import java.util.UUID;

import static com.alaraiscan.slate.R.layout.activity_bluetooth_chat;

public class BluetoothChat extends AppCompatActivity implements View.OnClickListener {
    Button send;


    EditText message;
    boolean mBounded;
    BluetoothService mServer;

    private static final UUID my_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_bluetooth_chat);
        send = (Button) findViewById(R.id.send);
        message = (EditText)findViewById(R.id.message);
        send.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        String res = message.getText().toString();
        message.setText("");
        mServer.sendData(res);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, BluetoothService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            LocalBinder mLocalBinder = (LocalBinder)service;
            mServer = mLocalBinder.getService(); // Get local server instance
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };



}
