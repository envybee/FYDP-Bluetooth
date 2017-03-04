package com.group15.shadowkart.shadowkart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by srswamy on 2017-03-01.
 */
public class BluetoothClientConnection  extends Thread {
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mAdapter;
    private final UUID myUUID;

    public BluetoothClientConnection(BluetoothAdapter mAdapter, BluetoothDevice device, UUID myUUID) {
        BluetoothSocket tmp = null;
        this.mDevice = device;
        this.myUUID = myUUID;
        this.mAdapter = mAdapter;

        try {
            tmp = device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection
        mAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through socket.
            // This call blocks until it succeeds or throws exception.
            mSocket.connect();

        } catch (IOException e) {

            e.printStackTrace();

            try {
                mSocket.close();
            } catch(IOException ce) {
                ce.printStackTrace();
            }

            return;
        }

        // Successful connection attempt
        manageMyConnectedSocket(mSocket);
    }

    // Closes the client socket and causes the thread to finish
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void manageMyConnectedSocket(BluetoothSocket mSocket) {
        try {
            OutputStream outStream = mSocket.getOutputStream();
            outStream.write((new String("Rezaan you dumb bitch")).getBytes());
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
