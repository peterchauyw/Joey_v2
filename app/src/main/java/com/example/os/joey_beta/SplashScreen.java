package com.example.os.joey_beta;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by OS on 10/05/2016.
 */
public class SplashScreen extends Activity {

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Log.d("Before Thread", "Starting time thread");
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Log.d("After Thread", "Or this?");
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
        Log.d("After Thread", "Thread finishing");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

