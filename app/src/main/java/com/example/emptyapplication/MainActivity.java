package com.example.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = 0;
        // 빈 데이터 리스트 생성.
        final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;
        // listview 생성 및 adapter 지정.
        final ListView devList = (ListView) findViewById(R.id.deviceList) ;
        devList.setAdapter(adapter) ;

        final TextView testText = (TextView) findViewById(R.id.testTextView);
        Button btnRefresh = (Button) findViewById(R.id.btnRefresh) ;
        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                testText.setText("Refresh") ;
                testText.setTextColor(Color.rgb(255, 0, 0));
            }
        }) ;

        Button btnSelect = (Button) findViewById(R.id.btnSelect) ;
        btnSelect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                testText.setText("Select") ;
                testText.setTextColor(Color.rgb(255, 255, 0));

                count++;
                // 아이템 추가.
                items.add("LIST" + Integer.toString(count));
                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        }) ;

        final TextView inputText = (TextView) findViewById(R.id.singleLineInput);
        final TextView textLog = (TextView) findViewById(R.id.textLog);
        Button btnEnter = (Button) findViewById(R.id.btnEnter) ;
        btnEnter.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                textLog.append(inputText.getText() + "\n");
                inputText.setText("") ;
            }
        }) ;

        BluetoothSerialClient client = BluetoothSerialClient.getInstance();
        if(client == null) {
            // 블루투스를 사용할 수 없는 장비일 경우 null.
            Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        if(!client.isEnabled()) {
            // 블루투스 사용을 활성화 한다.
            // 사용자에게 블루투스 사용을 묻는 시스템 창을 출력하게 된다.
            //블루투스가 사용가능한 상태이면 무시한다.
            client.enableBluetooth(MainActivity.this,
                    new BluetoothSerialClient.OnBluetoothEnabledListener() {
                        @Override
                        public void onBluetoothEnabled(boolean success) {
                            // sucess 가 false 일 경우 사용자가 블루투스 사용하기를 희망하지 않음.
                        }
                    });
        }

        final ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
        // 이미 페어링된 디바이스 리스트를 가져온다.
        Set<BluetoothDevice> pairedDevices =  client.getPairedDevices();
        deviceList.addAll(pairedDevices);
        // 근처 디바이스를 스캔한다.
        client.scanDevices(this, new BluetoothSerialClient.OnScanListener() {
            @Override
            public void onStart() {
                // 스캔 시작.
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
                    items.add(bluetoothDevice.getName());
                    // listview 갱신
                    adapter.notifyDataSetChanged();
                }
                catch(SecurityException e) {
                    // TODO
                }
            }

            @Override
            public void onFinish() {
                // 스캔 종료.
            }
        });
        // 아래와 같이 스캔 도중에 취소할 수 있다.
        //client.cancelScan(this)
    }
}