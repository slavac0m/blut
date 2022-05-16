package com.example.blut;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blut.adapter.BtCONST;
import com.example.blut.blut.BtConnect;
import com.example.blut.blut.ConnectThread;
import com.example.blut.blut.ReceiveThread;

import javax.net.ssl.SSLEngineResult;

public class MainActivity extends AppCompatActivity {
    private MenuItem menuItem;
    private MenuItem ConnectStatus;
    private BluetoothAdapter btAdapter;
    private final int REQUEST_BL = 15;
    private SharedPreferences preferences;
    private BtConnect btConnect;
    private Button bA, bB, bC, bD;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bB = findViewById(R.id.ButB);
        bA = findViewById(R.id.ButA);
        bC = findViewById(R.id.ButC);
        bD = findViewById(R.id.ButD);
        init();
        actionBar = getSupportActionBar();



        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.ButA:
                        btConnect.sendMess("0");
                        break;
                    case R.id.ButB:
                        btConnect.sendMess("1");
                        break;
                    case R.id.ButC:
                        btConnect.sendMess("2");
                        break;
                    case R.id.ButD:
                        btConnect.sendMess("3   ");
                        break;
                }
            }
        };
        bA.setOnClickListener(clickListener);
        bB.setOnClickListener(clickListener);
        bC.setOnClickListener(clickListener);
        bD.setOnClickListener(clickListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(receiver, filter);
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                ConnectStatus.setIcon(R.drawable.ic_circle_green);
                try {
                    actionBar.setTitle( btAdapter.getName());
                } catch (SecurityException e){

                }

            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)){
                ConnectStatus.setIcon(R.drawable.ic_circle_red);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.bt_id);
        ConnectStatus = menu.findItem(R.id.id_status);
        setBtIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bt_id) {
            if (!btAdapter.isEnabled()) {
                enableBt();
            } else {
                try {
                    btAdapter.disable();
                    menuItem.setIcon(R.drawable.ic_baseline_bluetooth_24);
                } catch (SecurityException e){}

            }
        }else if (item.getItemId()==R.id.bt_menu){
            if (btAdapter.isEnabled()){
                Intent intent = new Intent(MainActivity.this, Bt2.class);
                startActivity(intent);
            } else {
                Toast.makeText(this,"Включи Bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId()==R.id.id_connect){
            btConnect.connecting();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BL) {
            if (requestCode == RESULT_OK) {
                setBtIcon();
            }
        }
    }

    private void setBtIcon() {
        if (btAdapter.isEnabled()) {
            menuItem.setIcon(R.drawable.ic_baseline_bluetooth_disabled_24);
        } else {
            menuItem.setIcon(R.drawable.ic_baseline_bluetooth_24);
        }
    }

    private void init() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        preferences = getSharedPreferences(BtCONST.MY_PREF, Context.MODE_PRIVATE);
        btConnect = new BtConnect(this);
    }

    private void enableBt() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(intent, REQUEST_BL);
        } catch (SecurityException e){}
        setBtIcon();
    }

}