package com.example.emptyapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int count;

    public ListView mDevicesListView;
    private Button btnRefresh;
    private Button btnSelect;
    public TextView mBluetoothStatus;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BluetoothFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BluetoothFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BluetoothFragment newInstance(String param1, String param2) {
        BluetoothFragment fragment = new BluetoothFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        count = 0;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // GUI Id 가져오기
        mDevicesListView = (ListView) v.findViewById(R.id.deviceList) ;
        btnRefresh = (Button) v.findViewById(R.id.btnRefresh) ;
        btnSelect = (Button) v.findViewById(R.id.btnSelect) ;
        mBluetoothStatus = (TextView)v.findViewById(R.id.bluetooth_status);

        Bundle result = new Bundle();
        result.putString("bundleKey", "test bluetooth result");
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("BluetoothUiCreated", result);

        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putString("bundleKey", "result");
                // The child fragment needs to still set the result on its parent fragment manager
                getParentFragmentManager().setFragmentResult("requestKey", result);

                Toast.makeText(getContext(), "Get Paired Device...", Toast.LENGTH_SHORT).show();
            }
        }) ;

        btnSelect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                Log.i("TEST", "Append " + Integer.toString(count));
                System.out.println("toast Append " + Integer.toString(count));
                // listview 갱신
                // mBTArrayAdapter.notifyDataSetChanged();
            }
        }) ;

        // Inflate the layout for this fragment
        return v;
    }

    public void changeBluetoothStatus(String s)
    {
        mBluetoothStatus.setText(s);
    }

}