package com.group15.shadowkart.shadowkart;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static String BLUETOOTH_DEVICE_NAME = "Shadow Kart";
    State skState = State.UNKNKOWN;
    BluetoothDevice mmDevice = null;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothClientConnection client = null;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_button);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sendValue(!isChecked);
                skState = State.OFF;
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        skState = State.OFF;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendValue(boolean val){
        skState = State.ON;

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth. Get a better phone.", Toast.LENGTH_SHORT).show();
        } else {

            // Enable Bluetooth if it's not enabled
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // Search for paired devices
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(BLUETOOTH_DEVICE_NAME)) {
                        mmDevice = device;
                        break;
                    }
                }
            }

            // Get a client and run it
            try {
                if (client == null) {
                    client = new BluetoothClientConnection(mBluetoothAdapter,
                            mmDevice, UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ef"), this);
                }
                client.doInBackground(toggleButton, val);
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

}
