package com.snap.thirdear.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {

    private static final UUID MY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String RASPBERRYPI = "raspberrypi";
    private  BluetoothAdapter mBluetoothAdapter;
    public static  InputStream mmInStream;
    public static OutputStream mmOutStream;
    byte[] buffer;
    BluetoothDevice piDevice;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.d("BluetoothService", "pairedDevices.size()= " + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equalsIgnoreCase(RASPBERRYPI)){
                    Log.d("BluetoothService", "Devices= " + device);
                    Log.d("BluetoothService", "Devices name= " + device.getName());
                    Log.d("BluetoothService", "Devices Addr= " + device.getAddress());
                    piDevice = device;
                    break;
                }
            }
            if(null != piDevice) {
                ConnectThread ct = new ConnectThread(piDevice);
                ct.start();
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (null != mmInStream)
                mmInStream.close();
            if (null != mmOutStream)
                mmOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                tmp = (BluetoothSocket) m.invoke(mmDevice, 1);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Boolean isConnected = mmSocket.isConnected();
                Log.d("BT", isConnected.toString());
                //manageConnectedSocket(mmSocket)
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            // Get the BluetoothSocket input and output streams
            try {
                mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
                buffer = new byte[1024];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    public void write(String msg, OutputStream outStream) {
        byte[] buffer = msg.getBytes();
        try {
            //write the data to socket stream
            outStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   /* private void manageConnectedSocket(BluetoothSocket mmSocket) {
        //now make the socket connection in separate thread to avoid FC
        Thread connectionThread  = new Thread(new Runnable() {

            @Override
            public void run() {
                // Keep listening to the InputStream while connected
                while (true) {
                    try {
                        //read the data from socket stream
                        mmInStream.read(buffer);
                        // Send the obtained bytes to the UI Activity
                    } catch (IOException e) {
                        //an exception here marks connection loss
                        //send message to UI Activity
                        break;
                    }
                }
            }
        });

        connectionThread.start();
    }*/


}
