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
    /**
     * The constant B_DEVICE.
     */
    public static final String B_DEVICE = "MY DEVICE";
    /**
     * The constant B_UUID.
     */
    public static final String B_UUID = "00001101-0000-1000-8000-00805f9b34fb";
// 00000000-0000-1000-8000-00805f9b34fb

    /**
     * The constant STATE_NONE.
     */
    public static final int STATE_NONE = 0;
    /**
     * The constant STATE_LISTEN.
     */
    public static final int STATE_LISTEN = 1;
    /**
     * The constant STATE_CONNECTING.
     */
    public static final int STATE_CONNECTING = 2;
    /**
     * The constant STATE_CONNECTED.
     */
    public static final int STATE_CONNECTED = 3;

    private ConnectBtThread mConnectThread;
    private static ConnectedBtThread mConnectedThread;

    private static Handler mHandler = null;
    /**
     * The constant mState.
     */
    public static int mState = STATE_NONE;
    /**
     * The constant deviceName.
     */
    public static String deviceName;
    /**
     * The constant sDevice.
     */
    public static BluetoothDevice sDevice = null;
    /**
     * The Pack data.
     */
    public Vector<Byte> packData = new Vector<>(2048);

    /**
     * Instantiates a new Bluetooth service.
     *
     * @param mBluetoothAdapter the m bluetooth adapter
     */
    public BluetoothService(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    /**
     * Instantiates a new Bluetooth service.
     */
    public BluetoothService() {
        super();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //mHandler = getApplication().getHandler();
        return mBinder;
    }

    /**
     * Toast.
     *
     * @param mess the mess
     */
    public void toast(String mess){
        Toast.makeText(this,mess,Toast.LENGTH_SHORT).show();
    }
    private final IBinder mBinder = (IBinder) new LocalBinder();

    /**
     * The type Local binder.
     */
    public class LocalBinder extends Binder {
        /**
         * Gets service.
         *
         * @return the service
         */
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
        if (mHandler != null){
            // mHandler.obtainMessage();
        }
    }

    /**
     * Stop.
     */
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

    /**
     * Send data.
     *
     * @param message the message
     */
    public void sendData(String message){
        if (mConnectedThread!= null){
            mConnectedThread.write(message.getBytes());
            toast("Mesaj iletildi");
        }else {
            Toast.makeText(BluetoothService.this,"Mesaj iletilirken bir hata oluştu",Toast.LENGTH_SHORT).show();
        }
    }

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
/*
private synchronized void connected(BluetoothSocket mmSocket){

    if (mConnectThread != null){
        mConnectThread.cancel();
        mConnectThread = null;
    }
    if (mConnectedThread != null){
        mConnectedThread.cancel();
        mConnectedThread = null;
    }

    mConnectedThread = new ConnectedBtThread(mmSocket);
    mConnectedThread.start();


    setState(STATE_CONNECTED);
}*/

    private class ConnectBtThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        /**
         * Instantiates a new Connect bt thread.
         *
         * @param device the device
         */
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

        /**
         * Cancel.
         */
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

        /**
         * Instantiates a new Connected bt thread.
         *
         * @param socket the socket
         */
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


        /**
         * Write.
         *
         * @param buff the buff
         */
        public void write(byte[] buff){
            try {
                outS.write(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cancel(){
            try {
                cSocket.close();
                Log.d("service","connected thread cancel method");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        this.stop();
        super.onDestroy();
    }
}