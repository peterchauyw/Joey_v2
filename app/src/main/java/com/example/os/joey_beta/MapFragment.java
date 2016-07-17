package com.example.os.joey_beta;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by OS on 19/05/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    // Haste st. 37.866906, -122.252588
    double latitude = 37.866906;
    double longitude = -122.252588;
    LatLng latLng1;
    LatLng latLng2;

    Marker originMarker;
    Marker joeyMarker;

    PolylineOptions polylineOptions;
    Polyline polyline;

    boolean isJoeyMarkerCreated = false;
    //private static final String polyline = "gsqqFxxu_SyRlTys@npAkhAzY{MsVc`AuHwbB}Lil@}[goCqGe|BnUa`A~MkbG?eq@hRq}@_N}vKdB";

    private final int[] MAP_TYPES = { GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE };

    private int curMapTypeIndex = 1;
    private Intent mIntent;
    private MsgReceiver msgReceiver;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        initListeners();
    }

    private void initListeners(){
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);

        joeyMarker = getMap().addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .title("Joey is here!")
                .snippet(getAddressFromLatLng(new LatLng(latitude,longitude)))
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(),R.drawable.ic_rabbit_pin50))));

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        getActivity().registerReceiver(msgReceiver, intentFilter);
    }

    private void initCamera(Location location){
        latLng1 = new LatLng(getLatitude(), getLongitude());
        CameraPosition position = CameraPosition.builder()
                .target(latLng1)
                .zoom(20f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        getMap().clear();

        getMap().animateCamera(CameraUpdateFactory
                .newCameraPosition(position),null);

        getMap().setMapType(MAP_TYPES[curMapTypeIndex]);
        getMap().setTrafficEnabled(false);
        getMap().setBuildingsEnabled(true);

        /*

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMap().setMyLocationEnabled(true);

        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET}, 10);
        }*/

        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(true);
        //Toast.makeText(getActivity(),"Location = "+mCurrentLocation,Toast.LENGTH_LONG).show();

        originMarker = getMap().addMarker(new MarkerOptions()
                .position(new LatLng(getLatitude(),getLongitude()))
                .title("Where is joey?")
                .snippet(getAddressFromLatLng(new LatLng(getLatitude(),getLongitude())))
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(),R.drawable.ic_bear_pin50))));

        //drawCircle(new LatLng(getLatitude(),getLongitude()));


        getMap().setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //getMap().clear();
                originMarker.remove();
                originMarker = getMap().addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(),location.getLongitude()))
                        .title("Where is joey?")
                        .snippet(getAddressFromLatLng(new LatLng(getLatitude(),getLongitude())))
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource(getResources(),R.drawable.ic_bear_pin50))));
            }
        });



        polylineOptions = new PolylineOptions()
                .add(latLng1)
                .add(latLng1);
        polyline = getMap().addPolyline(polylineOptions
                .color(Color.CYAN));

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 10) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMap().setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getActivity(),"Access denied",Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Log.d("Current Location", ""+mCurrentLocation);
        //Log.d("Registered latitude", ""+mCurrentLocation.getLatitude());
        //Log.d("Registered longitude", ""+mCurrentLocation.getLongitude());
        initCamera(mCurrentLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public double getLatitude(){
        if(mCurrentLocation != null){
            latitude = mCurrentLocation.getLatitude();
        }else{
            latitude = 37.874879;
            Log.d("Latitude didn't pass.","LON");
        }

        return latitude;

    }
    public double getLongitude(){
        if(mCurrentLocation != null){
            longitude = mCurrentLocation.getLongitude();
        }else {
            longitude = -122.258529;
            Log.d("Latitude didn't pass.","LAT");
        }
        return longitude;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mCurrentLocation = new Location("");        // Berkeley: 37.874947, -122.258680
        mCurrentLocation.setLatitude(37.87497);
        mCurrentLocation.setLongitude(-122.084804);
        initCamera(mCurrentLocation);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        /*
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.defaultMarker());
        getMap().addMarker(options);*/

        Toast.makeText(getActivity()
                ,"Address: "+getAddressFromLatLng(latLng)+", location: "+latLng.latitude+", "+latLng.longitude+"."
                ,Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(),"Access denied",Toast.LENGTH_LONG).show();
        Log.d("BEARING: ",""+getMap().getCameraPosition().bearing);



    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        /*
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng(latLng) );

        options.icon( BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(
                        getResources(), R.drawable.ic_rabbit_pin50  ) ) );  // Default resource: R.mipmap.ic_launcher

        getMap().addMarker(options);
        latLng2 = new LatLng(latLng.latitude,latLng.longitude);*/

        /*

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(latLng1)
                .add(latLng2);

        Polyline polyline = getMap().addPolyline(polylineOptions
                .color(Color.CYAN));*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        drawCircle(new LatLng(getLatitude(),getLongitude()));
        return false;
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(mIntent);
        getActivity().unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );

        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }

    private void drawCircle( LatLng location ) {
        CircleOptions options = new CircleOptions();
        options.center( location );
        //Radius in meters
        options.radius(30);
        options.strokeWidth( 10 );
        //options.strokeColor(getResources().getColor(R.color.blue));
        options.strokeColor(Color.CYAN);
        //options.fillColor(getResources().getColor(R.color.blue));
        options.fillColor(Color.TRANSPARENT);
        getMap().addCircle(options);
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            double lat = Double.parseDouble(intent.getStringExtra("lat"));
            double lon = Double.parseDouble(intent.getStringExtra("lon"));

            Log.d("BT lat in map", ""+lat);
            Log.d("BT lon in map", ""+lon);

            joeyMarker.remove();
            joeyMarker = getMap().addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title("Joey is here!")
                    .snippet(getAddressFromLatLng(new LatLng(lat,lon)))
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(getResources(),R.drawable.ic_rabbit_pin50))));

/*
            polyline.remove();
            LatLng latLngMarker = new LatLng(getLatitude(),getLongitude());
            LatLng latLngJoey = new LatLng(lat,lon);


            polylineOptions.add(latLngMarker).add(latLngJoey);
            polyline = getMap().addPolyline(polylineOptions
                    .color(Color.CYAN));*/


        }
    }


}