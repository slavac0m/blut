package com.example.blut.blut;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.blut.MainActivity;

import java.io.IOException;


public class ConnectThread extends Thread {
    private ReceiveThread receiveThread;
    protected BluetoothAdapter btAdapter;
    protected MainActivity mainActivity;
    protected boolean ConnectStatus = false;
    public ConnectThread(){
        this.ConnectStatus = ConnectStatus;
    }
//
    public boolean isConnectStatus() {
        return ConnectStatus;
    }

    private BluetoothSocket mainSocket;

    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";


    ConnectThread(BluetoothAdapter btAdapter, BluetoothDevice device) {
        this.btAdapter = btAdapter;
        try {
            try {mainSocket = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));} catch (SecurityException e ){}
        } catch (IOException e){

        }
    }


    @Override
    public void run() {
        try { btAdapter.cancelDiscovery(); }catch (SecurityException e ){}
        try {
            try { mainSocket.connect();
                 receiveThread = new ReceiveThread(mainSocket);
                receiveThread.start();
            } catch (SecurityException e){}
            Log.d("MyLog", "Connected");
            ConnectStatus = true;
            Log.d("MyLog", "Connected"+ ConnectStatus);
//            mainActivity.StatusOk();
        }
        catch (IOException e ){
            Log.d("MyLog", "Not connected");
            Log.d("MyLog", "Connected"+ConnectStatus);
            ConnectStatus = false;
            closeConnection();
//            mainActivity.StatusNo();
        }
    }

    public void closeConnection(){
        try {
            mainSocket.close();

        } catch (IOException e){

        }
    }

    public ReceiveThread getReceiveThread(){
        return receiveThread;
    }
}
