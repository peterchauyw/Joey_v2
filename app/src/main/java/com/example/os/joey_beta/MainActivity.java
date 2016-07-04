package com.example.os.joey_beta;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.os.joey_beta.Services.BluetoothService;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    ArrayAdapter<String> arrayListPairedDevices;
    Boolean isBtEnabled = false;

    private static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address;

    private BluetoothSocket bluetoothSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment(), "");
        viewPagerAdapter.addFragments(new NavigationFragment(), "");
        viewPagerAdapter.addFragments(new MessageFragment(), "");
        viewPagerAdapter.addFragments(new CharacterFragment(), "");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        final int[] icons = new int[]{
                R.drawable.ic_home, R.drawable.ic_bino,
                R.drawable.ic_heart, R.drawable.ic_bear};

        for (int i = 0; i < 4; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        // Starting Bluetooth Request
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        arrayListPairedDevices = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice);

            if (bluetoothAdapter == null) {
            Log.d("Not supported device", "This device doesn't support bluetooth");
        } else {
            Log.d("Supported device", "This device supports bluetooth!");
            Log.d("Device bluetooth", bluetoothAdapter.getName() + ", " + bluetoothAdapter.getAddress());
        }

        if (!bluetoothAdapter.isEnabled()) {
            AlertDialog.Builder builderBluetooth = new AlertDialog.Builder(this);
            builderBluetooth.setTitle("Bluetooth permission");
            builderBluetooth.setMessage("This application requires bluetooth access.");

            builderBluetooth.setPositiveButton("Allow",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    bluetoothAdapter.enable();
                    isBtEnabled = true;
                    getPairedDevices();
                    //Toast.makeText(MainActivity.this, "Bluetooth activated", Toast.LENGTH_LONG).show();
                }
            });

            builderBluetooth.setNegativeButton("Quit",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Exit", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

            builderBluetooth.show();
        }else{
            getPairedDevices();
        }

        if(isBtEnabled){
            Log.d("BT", "Bt enabled!!!");
        }else{
            Log.d("BT", "Bt not enabled.");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //getPairedDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity", "onResume from Main Activity running...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(MainActivity.this, BluetoothService.class));
    }

    private void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        //If there are paired devices
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                arrayListPairedBluetoothDevices.add(device);
                arrayListPairedDevices.add(device.getName()+"\n"+device.getAddress());
                Log.d("Paired device", device.getName() + ", " + device.getAddress());
            }

                AlertDialog.Builder builderDevices = new AlertDialog.Builder(MainActivity.this);
                builderDevices.setTitle("Please, select device to connect. ");

                builderDevices.setAdapter(arrayListPairedDevices,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String deviceString = arrayListPairedDevices.getItem(which);
                                AlertDialog.Builder builderDevice= new AlertDialog.Builder(MainActivity.this);
                                builderDevice.setMessage(deviceString);
                                builderDevice.setTitle("Selected device: ");
                                Log.d("BT Issue", "device String: "+deviceString);
                                address = arrayListPairedBluetoothDevices.get(which).getAddress();
                                Log.d("BT Issue", "array Bt list device: "+address);
                                builderDevice.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intentService = new Intent(MainActivity.this,BluetoothService.class);
                                        intentService.putExtra("Address", address);
                                        //intentService.putExtra("Address",deviceString);
                                        startService(intentService);
                                        dialog.dismiss();
                                    }
                                });
                                builderDevice.show();
                            }
                        });
                builderDevices.show();
        } else {
            Log.d("BT", "No bluetooth paired devices");
        }
    }
}




/* Helpful code */


/****** Request for enabling Bluetooth Adapter **********/
/*
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/


/******** AlertDialog with create method ******/
/*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sure you wanna make decision?");

        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Good choice!", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Bad choice!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/

/***** AlertDialog with other inner one *******/

/*
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Paired devices");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("00:10:20:30:40");
        arrayAdapter.add("01:11:21:31:41");
        arrayAdapter.add("02:12:22:32:42");
        arrayAdapter.add("03:13:23:33:43");
        arrayAdapter.add("05:15:25:35:45");

        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String deviceString = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderDevices = new AlertDialog.Builder(MainActivity.this);
                        builderDevices.setMessage(deviceString);
                        builderDevices.setTitle("Selected item: ");
                        builderDevices.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderDevices.show();
                    }
                });
        builderSingle.show();
        */

/**** is BluetoothAdapter Enabled *****/

/*
if (!bluetoothAdapter.isEnabled()) {
            AlertDialog.Builder builderBluetooth = new AlertDialog.Builder(this);
            builderBluetooth.setTitle("Bluetooth permission");
            builderBluetooth.setMessage("This application requires bluetooth access.");

            builderBluetooth.setPositiveButton("Allow",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    bluetoothAdapter.enable();

                    new CountDownTimer(30000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            //mTextField.setText("done!");
                            getPairedDevices();
                        }
                    }.start();

                    //getPairedDevices();
                    //Toast.makeText(MainActivity.this, "Bluetooth activated", Toast.LENGTH_LONG).show();
                }
            });

            builderBluetooth.setNegativeButton("Quit",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Exit", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

            builderBluetooth.show();
        }else{
            getPairedDevices();
        }
 */