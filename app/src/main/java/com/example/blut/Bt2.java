package com.example.blut;

import static com.example.blut.adapter.BtAdapter.TITLE_ITEM_TYPE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.blut.adapter.BtAdapter;
import com.example.blut.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Bt2 extends AppCompatActivity {
    private final int BT_REQUEST_PERM = 123;
    private ListView listView;
    private BtAdapter adapter;
    private BluetoothAdapter btAdapter;
    private List<ListItem> list;
    private boolean isBtPermission = false;
    private boolean isDiscovery = false;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt2);
        getBtPermission();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        IntentFilter f3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, f1);
        registerReceiver(broadcastReceiver, f2);
        registerReceiver(broadcastReceiver, f3);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isDiscovery) {
                try {
                    btAdapter.cancelDiscovery();
                } catch (SecurityException e) {
                }
                isDiscovery = false;
                getPairedDevices();

            } else {

                finish();
            }
        } else if (item.getItemId() == R.id.id_search) {
            if (isDiscovery) return true;
            actionBar.setTitle(R.string.discovering);
            list.clear();
            ListItem itemTytle = new ListItem();
            itemTytle.setItemType(BtAdapter.TITLE_ITEM_TYPE);
            list.add(itemTytle);
            adapter.notifyDataSetChanged();
            try { btAdapter.startDiscovery(); } catch (SecurityException e) {}
            isDiscovery = true;
        }

        return true;
    }


    private void init() {
        actionBar = getSupportActionBar();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listV);
        adapter = new BtAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(adapter);
        getPairedDevices();
        onItemClickListener();
    }

    private void onItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem item = (ListItem) adapterView.getItemAtPosition(i);
                if (item.getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)) {
                    try {
                        item.getBluetoothDevice().createBond();
                    } catch (SecurityException e) {
                    }
                }
            }
        });
    }


    private void getPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            list.clear();
            for (BluetoothDevice device : pairedDevices) {
                ListItem item = new ListItem();
                item.setBluetoothDevice(device);
                list.add(item);
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BT_REQUEST_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isBtPermission = true;
            } else {
                Toast.makeText(this, "No request", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getBtPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, BT_REQUEST_PERM);
        } else {
            isBtPermission = true;
        }


    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ListItem item = new ListItem();
                item.setBluetoothDevice(device);
                item.setItemType(BtAdapter.DISCOVERY_ITEM_TYPE);
                list.add(item);
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                isDiscovery = false;
                getPairedDevices();
                actionBar.setTitle(R.string.app_name);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    if (device.getBondState() ==BluetoothDevice.BOND_BONDED);{
                        getPairedDevices();
                    }
                } catch (SecurityException e){}
            }
        }
    };
}
