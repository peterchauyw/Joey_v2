package com.example.os.joey_beta.Services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import android.support.annotation.Nullable;

import com.example.os.joey_beta.MainActivity;

/* Created by OS on 24/06/2016. */

public class BluetoothService extends Service {

    //private final Context ctx;

    public static String BT_TAG = "BLUETOOTH_ISSUE";
    public static String ACTION = "ACTION_GETDATA";
    private ConnectedThread mConnectedThread;
    private BluetoothSocket bluetoothSocket = null;
    Handler bluetoothIn;
    //Timer taskTimer;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    // SPP UUID service, should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;

    double trackerLatitude;
    double trackerLongitude;

    /*
    public BluetoothService(){
        super();
        this.ctx = this.getApplicationContext();
    }

    public BluetoothService(Context c){
        super();
        this.ctx = c;
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg){
                if (msg.what == handlerState){
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    //recDataString.trimToSize();
                    int endOfLineIndex = recDataString.indexOf("/");
                    int initialPosition = recDataString.indexOf("|");
                    //Log.d("Obtained string", ""+recDataString);
                    if(endOfLineIndex > 0){
                        String inputData = recDataString.substring(initialPosition,endOfLineIndex);
                        //Log.d("Received data", inputData);
                        Log.d("Data length", String.valueOf(inputData.length()));

                        if(inputData.charAt(0)=='|'){

                            String lat = recDataString.substring(1,10);
                            String lng = recDataString.substring(11,22);
                            String emoji = recDataString.substring(23,24);



                            Intent intent = new Intent("com.example.communication.RECEIVER");
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lng);
                            intent.putExtra("emoji", emoji);
                            sendBroadcast(intent);



                            Log.d("Latitude", lat);
                            Log.d("Longitude", lng);
                            Log.d("Emoji", emoji);

                            trackerLatitude = Double.parseDouble(lat);
                            trackerLongitude = Double.parseDouble(lng);
                            /*
                            Log.d("XXX", "Service inputData true "+ recDataString);
                            Log.d("XXX", "Service inputData sub "+ recDataString.substring(initialPosition, endOfLineIndex));
                            //String[] values = inputData.substring(1,inputData.length()).split("#");
                            String[] val = recDataString.substring(initialPosition+1,endOfLineIndex).split(",");
                            float valLat = Float.parseFloat(val[2]);
                            float valLng = Float.parseFloat(val[3]);

                            Intent intent = new Intent();
                            intent.setAction(BluetoothService.ACTION);
                            intent.putExtra("DATAPASSED",val[2]+","+val[3]);
                            sendBroadcast(intent);*/
                        }
                        recDataString.delete(0,recDataString.length());
                        inputData = " ";
                    }
                }
            }
        };

        Toast.makeText(getBaseContext(), "Bluetooth Service started", Toast.LENGTH_SHORT).show();
        Log.d("BL_TAG", "Service started");
        // Get the MAC address from DeviceList Activity via intent
        // Get the MAC address from the DeviceListActivity via Extra
        address = intent.getStringExtra("Address");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Create device and set the MAC address
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

        try{
            bluetoothSocket = createBluetoothSocket(bluetoothDevice);
        }catch(IOException e){
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }

        // Establishing the Bluetooth socket connection.
        try{
            bluetoothSocket.connect();
        }catch (IOException e){
            try{
                bluetoothSocket.close();
            }catch (IOException e1){
                Log.d(BT_TAG, "Bluetooth Socket couldn't close");
            }
        }
        mConnectedThread = new ConnectedThread(bluetoothSocket);
        mConnectedThread.start();
        Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();


        /*
        taskTimer = new Timer(true);
        taskTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stu
//				int XOR = '1'^','^'0'^','^'0'^','^'0'^','^'0'^',';
//				mConnectedThread.write("|1,0,0,0,0,#"+"XOR"+"/");
                int XOR = '1'^',';
                mConnectedThread.write("|1,#"+XOR+"/");
                Log.d("XXX Service ", "XOR "+ Integer.toString(XOR));
            }
        }, 5000, (long) (1000*LPS));*/

        return super.onStartCommand(intent, flags, startId);
    }

    public double getTrackerLatitude(){
        return trackerLatitude;
    }

    public double getTrackerLongitde(){
        return trackerLongitude;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        try{
            Log.d("XXX Service", "Closing Bluetooth Socket");
            bluetoothSocket.close();
            //taskTimer.cancel();
            //taskTimer.purge();
        }catch(IOException e){
            e.printStackTrace();
        }*/
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        //creates secure outgoing connection with BT device using UUID
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread{
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket){
            InputStream tmpInput = null;
            OutputStream tmpOutput = null;

            try{
                tmpInput = socket.getInputStream();
                tmpOutput = socket.getOutputStream();
            }catch (IOException e){ }
            inputStream = tmpInput;
            outputStream = tmpOutput;
        }

        public void run(){
            byte[] buffer = new byte[512];
            int bytes;

            while (true){
                try{
                    bytes = inputStream.read(buffer);   // Read bytes from input buffer
                    String readMessage = new String(buffer,0,bytes);
                    //Log.d("Reading MSG", readMessage);
                    bluetoothIn.obtainMessage(handlerState,bytes,-1,readMessage).sendToTarget();
                }catch (IOException e){
                    break;
                }
            }
        }
        // Write method
        public void write(String input){
            byte[] msgBuffer = input.getBytes();    // It converts entered string into bytes
            try{
                outputStream.write(msgBuffer);      // It writes bytes over Bt connection via outstream
            }catch (IOException e){
                try{
                    bluetoothSocket.close();
                    Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
    }
}



/***** Backup of bluetoothIn *****/
/*
bluetoothIn = new Handler(){
public void handleMessage(android.os.Message msg){
        if (msg.what == handlerState){
        String readMessage = (String) msg.obj;
        recDataString.append(readMessage);
        recDataString.trimToSize();
        int endOfLineIndex = recDataString.indexOf("/");
        int initialPosition = recDataString.indexOf("|");
        Log.d("Obtained string", ""+recDataString);
        if(endOfLineIndex > 0){
        String value = recDataString.substring(initialPosition,endOfLineIndex);
        if(value.charAt(0)=='|'){
        Log.d("XXX", "Service value true "+ recDataString);
        Log.d("XXX", "Service value sub "+ recDataString.substring(initialPosition, endOfLineIndex));
        //String[] values = value.substring(1,value.length()).split("#");
        String[] val = recDataString.substring(initialPosition+1,endOfLineIndex).split(",");
        float valLat = Float.parseFloat(val[2]);
        float valLng = Float.parseFloat(val[3]);

        Intent intent = new Intent();
        intent.setAction(BluetoothService.ACTION);
        intent.putExtra("DATAPASSED",val[2]+","+val[3]);
        sendBroadcast(intent);
        }
        recDataString.delete(0,recDataString.length());
        value = "";
        }
        }
        }
        };
*/