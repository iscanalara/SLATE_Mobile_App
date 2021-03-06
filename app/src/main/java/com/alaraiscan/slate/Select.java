package com.alaraiscan.slate;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.pulltorefresh.PullToRefresh;

/**
 * The type Selecting bluetooth device.
 */
public class Select extends Activity implements PullToRefresh.OnRefreshListener {
    //bluetooth object
    private Bluetooth bt;
    //listview
    private ListView listView;
    //not found button
    private Button notFound;
    //paired devices list
    private List<BluetoothDevice> paired;
    //refreshing devices
    private PullToRefresh refresh;
    private boolean registered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;

        bt = new Bluetooth(this);
        bt.enableBluetooth();

        refresh = findViewById(R.id.pull_to_refresh);
        listView =  findViewById(R.id.list);
        notFound = findViewById(R.id.not_in_list);

        refresh.setListView(listView);
        refresh.setOnRefreshListener(this);
        refresh.setSlide(500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent serviceIntent = new Intent(Select.this, BluetoothService.class);
                String adress = bt.getPairedDevices().get(position).getAddress();
                serviceIntent.putExtra("bluetooth_device", adress);
                if(registered) {
                    unregisterReceiver(mReceiver);
                    registered=false;
                }
                startService(serviceIntent);
                finish();
            }
        });

        notFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this, Scan.class);
                startActivity(intent);
            }
        });

        addBtDevicesToList();
    }

    //add paired devices to list
    private void addBtDevicesToList() {
        paired = bt.getPairedDevices();

        List<String> names = new ArrayList<>();
        for (BluetoothDevice d : paired){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array);

        listView.setAdapter(adapter);

        notFound.setEnabled(true);
    }

    //refreshing list
    @Override
    public void onRefresh() {
        List<String> names = new ArrayList<String>();
        for (BluetoothDevice d : bt.getPairedDevices()){
            names.add(d.getName());
        }

        String[] array = names.toArray(new String[names.size()]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, array);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.removeViews(0, listView.getCount());
                listView.setAdapter(adapter);
                paired = bt.getPairedDevices();
            }
        });
        refresh.refreshComplete();

    }


    //to listen to the broadcast signals of bluetooth
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setEnabled(false);
                            }
                        });
                        Toast.makeText(Select.this, "Bluetooth'u açınız.", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addBtDevicesToList();
                                listView.setEnabled(true);
                            }
                        });
                        break;
                }
            }
        }
    };


    // when connection destroy
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }

}
