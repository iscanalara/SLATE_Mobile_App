package com.alaraiscan.slate;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Handler;

/**
 * The type Bluetooth service.
 */
public class BluetoothService extends Service {

    private BluetoothAdapter mBluetoothAdapter;

    public static final String B_DEVICE = "MY DEVICE";

    //Bluetooth uuid
    public static final String B_UUID = "00001101-0000-1000-8000-00805f9b34fb";


    // Connection States
    public static final int STATE_NONE = 0;

    public static final int STATE_LISTEN = 1;

    public static final int STATE_CONNECTING = 2;

    public static final int STATE_CONNECTED = 3;


    private ConnectBtThread mConnectThread;
    private static ConnectedBtThread mConnectedThread;

    private static Handler mHandler = null;

    public static int mState = STATE_NONE;

    public static String deviceName;

    public static BluetoothDevice sDevice = null;

    public Vector<Byte> packData = new Vector<>(2048);

    //Instantiates a new Bluetooth service.
    public BluetoothService(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    //Instantiates a new Bluetooth service.

    public BluetoothService() {
        super();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void toast(String mess){
        Toast.makeText(this,mess,Toast.LENGTH_SHORT).show();
    }
    private final IBinder mBinder = (IBinder) new LocalBinder();

    //The type Local binder.
    public class LocalBinder extends Binder {
        //Gets service.

        BluetoothService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String deviceg = intent.getStringExtra("bluetooth_device");


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectToDevice(deviceg);

        return START_STICKY;
    }

    //The method of connecting the bluetooth service.
    //We establish a new connection with the mac address of the device selected from the menu
    //Creating two objects from the ConnectedBtThread class, mConnectThread and mConnectedThread
    //The first one represents that the connection has been established and the connection is in progress.
    private synchronized void connectToDevice(String macAddress){
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (mState == STATE_CONNECTING){
            if (mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectBtThread(device);
        toast("Bağlanıyor...");
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    private void setState(int state){
        mState = state;
    }

    //Stop
    public synchronized void stop(){
        setState(STATE_NONE);
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }

        stopSelf();
    }

    // Sending data using bluetooth.
    public void sendData(String message){
        if (mConnectedThread!= null){
            mConnectedThread.write(message.getBytes());
            toast("Mesaj iletildi");
        }else {
            Toast.makeText(BluetoothService.this,"Mesaj iletilirken bir hata oluştu",Toast.LENGTH_SHORT).show();
        }
    }

    //Stopping connected or ongoing situations
    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);

        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    // Bluetooth Thread Class
    private class ConnectBtThread extends Thread{
        //A connected or connecting Bluetooth socket.
        private final BluetoothSocket mSocket;
        //Represents a remote Bluetooth device
        private final BluetoothDevice mDevice;

        // Instantiates a new Connect bt thread.

        public ConnectBtThread(BluetoothDevice device){
            mDevice = device;
            BluetoothSocket socket = null;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(B_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = socket;

        }

        @Override
        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.d("service","connect thread run method (connected)");
                SharedPreferences pre = getSharedPreferences("BT_NAME",0);
                pre.edit().putString("bluetooth_connected",mDevice.getName()).apply();

            } catch (IOException e) {

                try {
                    mSocket.close();
                    Log.d("service","connect thread run method ( close function)");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            //connected(mSocket);
            mConnectedThread = new ConnectedBtThread(mSocket);
            mConnectedThread.start();
        }

        // Cancel
        public void cancel(){

            try {
                mSocket.close();
                Log.d("service","connect thread cancel method");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedBtThread extends Thread{
        private final BluetoothSocket cSocket;
        private final InputStream inS;
        private final OutputStream outS;

        private byte[] buffer;

        //Instantiates a new Connected bt thread.

        public ConnectedBtThread(BluetoothSocket socket){
            cSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inS = tmpIn;
            outS = tmpOut;
            //Bluetooth connected
            Intent intent = new Intent(BluetoothService.this,MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void run() {
            buffer = new byte[1024];
            int mByte;
            try {
                mByte= inS.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("service","connected thread run method");

        }


        // Write

        public void write(byte[] buff){
            try {
                outS.write(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //cancel
        private void cancel(){
            try {
                cSocket.close();
                Log.d("service","connected thread cancel method");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //to finalise the event
    @Override
    public void onDestroy() {
        this.stop();
        super.onDestroy();
    }
}
