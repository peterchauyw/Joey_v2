package com.example.os.joey_beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by OS on 10/05/2016.
 */
public class NavigationFragment extends Fragment{

    Button btNavigate;
    Boolean isBtPressed = false;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        btNavigate = (Button)view.findViewById(R.id.btLocation);

        final MapFragment mapFragment = new MapFragment();
        final NavigatorFragment navigatorFragment = new NavigatorFragment();

        FragmentTransaction firstTransaction = getFragmentManager().beginTransaction();
        firstTransaction.replace(R.id.container,navigatorFragment);
        firstTransaction.addToBackStack(null);
        firstTransaction.commit();

        btNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBtPressed = !isBtPressed;
                Log.d("Button","Pressed!, "+isBtPressed);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                if(isBtPressed){
                    btNavigate.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bt_nav));
                    transaction.replace(R.id.container,mapFragment);
                }else if(isBtPressed == false){
                    //NavigatorFragment navigatorFragment = new NavigatorFragment();
                    //FragmentTransaction backTransaction = getFragmentManager().beginTransaction();
                    btNavigate.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_bt_nav_p));
                    transaction.replace(R.id.container,navigatorFragment);
                    //backTransaction.replace(R.id.container,navigatorFragment);
                    //backTransaction.addToBackStack(null);
                    //backTransaction.commit();
                }
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
