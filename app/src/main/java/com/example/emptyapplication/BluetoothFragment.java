package com.example.emptyapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        count = 0;
        // 빈 데이터 리스트 생성.
        final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_single_choice, items) ;
        // listview 생성 및 adapter 지정.
        final ListView devList = (ListView) v.findViewById(R.id.deviceList) ;
        devList.setAdapter(adapter) ;

        Button btnRefresh = (Button) v.findViewById(R.id.btnRefresh) ;
        btnRefresh.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Toast...", Toast.LENGTH_SHORT).show();
                System.out.println("toast Refresh");
            }
        }) ;

        Button btnSelect = (Button) v.findViewById(R.id.btnSelect) ;
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
        // Inflate the layout for this fragment
        return v;
    }
}