package com.group15.shadowkart.shadowkart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by srswamy on 2017-03-01.
 */
public class BluetoothClientConnection  extends AsyncTask {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mAdapter;
    private final UUID myUUID;
    private final Context context;
    private ToggleButton toggleButton;

    public BluetoothClientConnection(BluetoothAdapter mAdapter, BluetoothDevice device, UUID myUUID, Context context) {
        BluetoothSocket tmp = null;
        this.mDevice = device;
        this.myUUID = myUUID;
        this.mAdapter = mAdapter;
        this.context = context;

        try {
            tmp = device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSocket = tmp;
    }

    // Closes the client socket and causes the thread to finish
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void manageMyConnectedSocket(BluetoothSocket mSocket, boolean val) {
        try {
            OutputStream outStream = mSocket.getOutputStream();
            outStream.write(Boolean.toString(val).getBytes());
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        this.toggleButton.setEnabled(false);
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        // Cancel discovery because it otherwise slows down the connection
        mAdapter.cancelDiscovery();
        this.toggleButton = (ToggleButton) objects[0];
        if (!mSocket.isConnected()) {
            try {
                // Connect to the remote device through socket.
                // This call blocks until it succeeds or throws exception.
                Toast.makeText(context, "Connecting", Toast.LENGTH_SHORT).show();
                mSocket.connect();

            } catch (IOException e) {

                e.printStackTrace();

                try {
                    mSocket.close();
                } catch(IOException ce) {
                    ce.printStackTrace();
                }

            }
        }

        // Successful connection attempt
        manageMyConnectedSocket(mSocket, (Boolean) objects[1]);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        this.toggleButton.setEnabled(true);
        super.onPostExecute(o);
    }
}
