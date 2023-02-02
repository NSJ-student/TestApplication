package com.example.emptyapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import com.example.emptyapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // #defines for identifying shared types between calling functions
    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    public BluetoothAdapter mBTAdapter;
    public ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    public Handler mHandler; // Our main handler that will receive callback notifications
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private BluetoothFragment fragBluetooth;
    private ChatFragment fragChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide appbar
        getSupportActionBar().hide();

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        fragBluetooth = new BluetoothFragment();
        fragChat = new ChatFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.frame, fragBluetooth).commit();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0)
                {
                    selected = fragBluetooth;
                }
                else if (position == 1)
                {
                    selected = fragChat;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        mBTArrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1) ;
        // get a handle on the bluetooth radio
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
        }
        else
        {
            bluetoothOn();

            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg){
                    if(msg.what == MESSAGE_READ){
                        String readMessage = null;
                        readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                        fragChat.setRecvMessage(readMessage);
                    }

                    if(msg.what == CONNECTING_STATUS){
                        char[] sConnected;
                        if(msg.arg1 == 1)
                            fragBluetooth.changeBluetoothStatus(getString(R.string.BTConnected) + msg.obj);
                        else
                            fragBluetooth.changeBluetoothStatus(getString(R.string.BTconnFail));
                    }
                }
            };

            getSupportFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                    // We use a String here, but any type that can be put in a Bundle is supported
                    String result = bundle.getString("bundleKey");
                    // Do something with the result
                    listPairedDevices();
                }
            });

            getSupportFragmentManager().setFragmentResultListener("BluetoothUiCreated", this, new FragmentResultListener() {
                        @Override
                public void onFragmentResult(@NonNull String BluetoothUiCreated, @NonNull Bundle bundle) {
                    // We use a String here, but any type that can be put in a Bundle is supported
                    String result = bundle.getString("bundleKey");
                    fragBluetooth.changeBluetoothStatus(result);
                    // Do something with the result
                    //  Bluetooth 탭의 리스트에 adapter 지정.
                    fragBluetooth.mDevicesListView.setAdapter(mBTArrayAdapter) ;
                    fragBluetooth.mDevicesListView.setOnItemClickListener(mDeviceClickListener);
                }
            });

            getSupportFragmentManager().setFragmentResultListener("messageWriting", this, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String BluetoothUiCreated, @NonNull Bundle bundle) {
                    // We use a String here, but any type that can be put in a Bundle is supported
                    String result = bundle.getString("bundleKey");
                    // Do something with the result
                    if(mConnectedThread != null) //First check to make sure thread created
                    {
                        mConnectedThread.write(result);
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        fragBluetooth.changeBluetoothStatus("not connected");
                    }
                }
            });
        }
    }

    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),getString(R.string.sBTturON),Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),getString(R.string.BTisON),Toast.LENGTH_SHORT).show();
        }
    }

    public void listPairedDevices(){
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
            {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

            fragBluetooth.changeBluetoothStatus(getString(R.string.show_paired_devices));
        }
        else
            fragBluetooth.changeBluetoothStatus(getString(R.string.BTnotOn));
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                fragBluetooth.changeBluetoothStatus(getString(R.string.BTnotOn));
                return;
            }

            fragBluetooth.changeBluetoothStatus(getString(R.string.cConnet));
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
                        fragBluetooth.changeBluetoothStatus(getString(R.string.ErrSockCrea));
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
                            fragBluetooth.changeBluetoothStatus(getString(R.string.ErrSockCrea));
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
            fragBluetooth.changeBluetoothStatus("Could not create Insecure RFComm Connection");
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

}