package com.example.emptyapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private static ListView mDevicesListView;
    private static Button btnRefresh;
    private static Button btnSelect;

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
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        mBTArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1) ;
        // get a handle on the bluetooth radio
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothOn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // GUI Id 가져오기
        mDevicesListView = (ListView) v.findViewById(R.id.deviceList) ;
        btnRefresh = (Button) v.findViewById(R.id.btnRefresh) ;
        btnSelect = (Button) v.findViewById(R.id.btnSelect) ;
        //  adapter 지정.
        mDevicesListView.setAdapter(mBTArrayAdapter) ;
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPairedDevices();
                Toast.makeText(getContext(), "Paired Device...", Toast.LENGTH_SHORT).show();
            }
        }) ;

        btnSelect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                Log.i("TEST", "Append " + Integer.toString(count));
                System.out.println("toast Append " + Integer.toString(count));
                // 아이템 추가.
                mBTArrayAdapter.add("LIST" + Integer.toString(count));
                // listview 갱신
                // mBTArrayAdapter.notifyDataSetChanged();
            }
        }) ;
        // Inflate the layout for this fragment
        return v;
    }

    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getContext(),getString(R.string.sBTturON),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),getString(R.string.BTisON), Toast.LENGTH_SHORT).show();
        }
    }

    private void listPairedDevices(){
        mBTArrayAdapter.clear();
        if(mBTAdapter==null)
        {
            System.out.println("mBTAdapter is null");
            return;
        }
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mPairedDevices==null)
        {
            System.out.println("mPairedDevices is null");
            return;
        }
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), getString(R.string.cConnet), Toast.LENGTH_LONG).show();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Could not create Insecure RFComm Connection", Toast.LENGTH_SHORT).show();
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}