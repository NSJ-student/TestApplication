package com.example.emptyapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView inputText;
    public TextView textLog;
    public Button btnEnter;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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

        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        inputText = (TextView) v.findViewById(R.id.singleLineInput);
        textLog = (TextView) v.findViewById(R.id.textLog);
        btnEnter = (Button) v.findViewById(R.id.btnEnter) ;

        btnEnter.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle result = new Bundle();
                result.putString("bundleKey", inputText.getText()+"");
                // The child fragment needs to still set the result on its parent fragment manager
                getParentFragmentManager().setFragmentResult("messageWriting", result);

                textLog.append(inputText.getText() + "\n");
                inputText.setText("") ;
            }
        }) ;
        // Inflate the layout for this fragment
        return v;
    }

    public void setRecvMessage(String s)
    {
        textLog.append(s);
    }
}