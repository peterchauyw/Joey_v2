package com.example.os.joey_beta;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.os.joey_beta.Services.BluetoothService;

import org.w3c.dom.Text;

/**
 * Created by OS on 10/05/2016.
 */
public class HomeFragment extends Fragment{

    TextView txtDistance;
    float defaultDistance = 1;
    float distance = 0;
    ImageView imgJoey;
    ImageView imgRabbit;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        txtDistance = (TextView)view.findViewById(R.id.txtDistance);
        imgJoey = (ImageView)view.findViewById(R.id.imageJoey);
        imgRabbit = (ImageView)view.findViewById(R.id.imageRabbit);

        //imgJoey.setY(10.0f);
        distance = defaultDistance;
        txtDistance.setText("Distance: "+defaultDistance);

        imgRabbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Joey distance", ""+imgJoey.getY());
                imgJoey.setY(imgJoey.getY()-10);
                distance+=1;
                txtDistance.setText("Distance: "+(int)distance+" ft");
            }
        });

        //return inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
