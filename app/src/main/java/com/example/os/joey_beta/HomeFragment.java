package com.example.os.joey_beta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.os.joey_beta.Services.BluetoothService;

import org.w3c.dom.Text;

/**
 * Created by OS on 10/05/2016.
 */
public class HomeFragment extends Fragment{

    TextView txtDistance;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        txtDistance = (TextView)view.findViewById(R.id.txtDistance);
        txtDistance.setText("10 ft");

        //return inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
