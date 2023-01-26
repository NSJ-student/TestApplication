package com.example.emptyapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide appbar
        getSupportActionBar().hide();

        count = 0;
        // 빈 데이터 리스트 생성.
        final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;
        // listview 생성 및 adapter 지정.
        final ListView devList = (ListView) findViewById(R.id.deviceList) ;
        devList.setAdapter(adapter) ;

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh) ;
        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Toast...", Toast.LENGTH_SHORT).show();
                System.out.println("toast Refresh");
            }
        }) ;

        Button btnSelect = (Button) findViewById(R.id.btnSelect) ;
        btnSelect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                Log.i("TEST", "Append " + Integer.toString(count));
                System.out.println("toast Append " + Integer.toString(count));
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
    }
}