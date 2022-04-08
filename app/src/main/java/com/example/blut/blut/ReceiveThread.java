package com.example.blut.blut;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ReceiveThread extends Thread{
    private BluetoothSocket socket;
    private InputStream Instream;
    private OutputStream outputStream;
    private  byte[] buffer;

    public ReceiveThread (BluetoothSocket socket){
        this.socket = socket;
        try {
            Instream = socket.getInputStream();
        } catch (IOException e){
        }

        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e){
        }
    }

    @Override
    public void run() {
        buffer = new byte[1000];
        while (true){
            try {
                 int size = Instream.read(buffer);
                String message = new String(buffer,0, size);
                Log.d("MyLog","Message: " + message);
            }catch (IOException e){
                break;
            }
        }
    }

    public void sendMess(byte[] byteArray){
        try {
            outputStream.write(byteArray);
        }catch (IOException e){

        }
    }
}
