package com.example.blut.blut;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.blut.adapter.BtCONST;

import java.nio.charset.StandardCharsets;

public class BtConnect {
    private Context context;
    private SharedPreferences sPreferences;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;



    private ConnectThread connectThread;


    public BtConnect(Context context) {
        this.context = context;
        sPreferences = context.getSharedPreferences(BtCONST.MY_PREF,Context.MODE_PRIVATE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connecting(){
        String mac = sPreferences.getString(BtCONST.MAC_KEY,"");
        if(!btAdapter.isEnabled() || mac.isEmpty()) return;
        device = btAdapter.getRemoteDevice(mac);

        if( device == null) return;
        connectThread = new ConnectThread(btAdapter, device);
        connectThread.start();
    }

    public void sendMess(String message){
        if (connectThread!= null){
            if (connectThread.getReceiveThread() != null) {
                try {
                    connectThread.getReceiveThread().sendMess(message.getBytes());
                } catch (Exception e) {
                }
            }
        }
    }
}
