package com.example.blut.blut;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.blut.Bt2;
import com.example.blut.MainActivity;
import com.example.blut.R;

import java.io.IOException;
import java.util.UUID;


public class ConnectThread extends Thread {
    private ReceiveThread receiveThread;
    protected BluetoothAdapter btAdapter;

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


        }
        catch (IOException e ){
            Log.d("MyLog", "Not connected");
            closeConnection();
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
