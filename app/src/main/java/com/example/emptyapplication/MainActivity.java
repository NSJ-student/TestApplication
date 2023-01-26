package com.example.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Set;

import com.example.emptyapplication.ui.main.SectionsPagerAdapter;
import com.example.emptyapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide appbar
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        System.out.println("블루투스 client를 가져온다");
        BluetoothSerialClient client = BluetoothSerialClient.getInstance();
        if(client == null) {
            // 블루투스를 사용할 수 없는 장비일 경우 null.
            Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없는 기기입니다.", Toast.LENGTH_LONG).show();
            System.out.println("블루투스를 사용할 수 없는 기기입니다");
        }
        if(!client.isEnabled()) {
            System.out.println("블루투스 사용을 활성화 한다");
            // 블루투스 사용을 활성화 한다.
            // 사용자에게 블루투스 사용을 묻는 시스템 창을 출력하게 된다.
            //블루투스가 사용가능한 상태이면 무시한다.
            client.enableBluetooth(MainActivity.this,
                    new BluetoothSerialClient.OnBluetoothEnabledListener() {
                        @Override
                        public void onBluetoothEnabled(boolean success) {
                            System.out.println("사용자가 블루투스 사용하기를 희망하지 않음");
                            // sucess 가 false 일 경우 사용자가 블루투스 사용하기를 희망하지 않음.
                        }
                    });
        }

        final ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
        // 이미 페어링된 디바이스 리스트를 가져온다.
        Set<BluetoothDevice> pairedDevices =  client.getPairedDevices();
        if(pairedDevices != null)
        {
            deviceList.addAll(pairedDevices);
        }
        else
        {
            System.out.println("페어링된 디바이스가 없다");
        }
        // 근처 디바이스를 스캔한다.
        client.scanDevices(this, new BluetoothSerialClient.OnScanListener() {
            @Override
            public void onStart() {
                // 스캔 시작.
                System.out.println("스캔 시작");
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                // 스캔이 완료된 디바이스를 받아온다.
                if(deviceList.contains(bluetoothDevice)) {
                    deviceList.remove(bluetoothDevice);
                }
                deviceList.add(bluetoothDevice);
                try {
                    // 아이템 추가.
                    System.out.println(bluetoothDevice.getName());
                }
                catch(SecurityException e) {
                    // TODO
                }
            }

            @Override
            public void onFinish() {
                // 스캔 종료.
                System.out.println("스캔 종료");
            }
        });

        // 아래와 같이 스캔 도중에 취소할 수 있다.
        //client.cancelScan(this)
        System.out.println("onCreate 종료");
    }
}