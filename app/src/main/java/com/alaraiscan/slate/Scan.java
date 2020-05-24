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

/**
 * The type Scan bluetooth device.
 */
public class Scan extends Activity implements Bluetooth.DiscoveryCallback, AdapterView.OnItemClickListener {

    //bluetooth object
    private Bluetooth bt;

    //list view
    private ListView listView;

    //adapter array
    private ArrayAdapter<String> adapter;

    //text view of state
    private TextView state;

    //progress bar
    private ProgressBar progress;

    //scan bar
    private Button scan;

    //list of bluetooth devices
    private List<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        listView = findViewById(R.id.scan_list);
        state =  findViewById(R.id.scan_state);
        progress = findViewById(R.id.scan_progress);
        scan = findViewById(R.id.scan_scan_again);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        bt = new Bluetooth(this);
        bt.setDiscoveryCallback(this);

        bt.scanDevices();
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
                //scan bluetooth devices
                bt.scanDevices();
            }
        });
    }

    // set text
    private void setText(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                state.setText(txt);
            }
        });
    }

    //set progress visibility
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
        //pair bt devices
        bt.pair(devices.get(position));
    }

    //when activity finish
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

    //add device
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

    //pair devices
    @Override
    public void onPair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Eşleşti!");
        Intent i = new Intent(Scan.this, Select.class);
        startActivity(i);
        finish();

    }

    //eşleşmeyi sonlandır
    @Override
    public void onUnpair(BluetoothDevice device) {
        setProgressVisibility(View.INVISIBLE);
        setText("Eşleşme sona erdi!");

    }

    //eşleşme sırasında hata mesajı
    @Override
    public void onError(String message) {
        setProgressVisibility(View.INVISIBLE);
        setText("Hata: "+message);

    }
}
