package com.alaraiscan.slate;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class Scan extends Activity implements Bluetooth.DiscoveryCallback, AdapterView.OnItemClickListener {

    private Bluetooth bluetooth;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private TextView state;
    private ProgressBar progress;
    private Button scan;
    private List<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        listView = (ListView)findViewById(R.id.scan_list);
        state = (TextView) findViewById(R.id.scan_state);
        progress = (ProgressBar) findViewById(R.id.scan_progress);
        scan = (Button) findViewById(R.id.scan_scan_again);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bluetooth = new Bluetooth(this);
        bluetooth.setDiscoveryCallback(this);

        bluetooth.scanDevices();
        progress.setVisibility(View.VISIBLE);
        state.setText("Aranıyor...");
        listView.setEnabled(false);

        scan.setEnabled(false);
        devices = new ArrayList<>();

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        scan.setEnabled(false);
                    }
                });

                devices = new ArrayList<>();
                progress.setVisibility(View.VISIBLE);
                state.setText("Aranıyor...");
                bluetooth.scanDevices();
            }
        });
    }


    private void setText(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                state.setText(txt);
            }
        });
    }

    private void setProgressVisibility(final int id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(id);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setProgressVisibility(View.VISIBLE);
        setText("Eşleşiyor...");
        bluetooth.pair(devices.get(position));
    }

    @Override
    public void onFinish() {
        setProgressVisibility(View.INVISIBLE);
        setText("Arama tamamlandı!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scan.setEnabled(true);
                listView.setEnabled(true);
            }
        });

    }

    @Override
    public void onDevice(BluetoothDevice device) {
        final BluetoothDevice tmp = device;
        devices.add(device);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(tmp.getAddress()+" - "+tmp.getName());
            }
        });

    }

    @Override
    public void onPair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Eşleşti!");
        Intent i = new Intent(Scan.this, Select.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onUnpair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Eşleşti!");

    }

    @Override
    public void onError(String message) {
        setProgressVisibility(View.INVISIBLE);
        setText("Hata: "+message);

    }
}
