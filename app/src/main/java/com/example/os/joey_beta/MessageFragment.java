package com.example.os.joey_beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.os.joey_beta.Services.ChatMessage;

import java.util.ArrayList;

/**
 * Created by OS on 10/05/2016.
 */
public class MessageFragment extends Fragment {

    ArrayList<ChatMessage> chatMessageArrayList;
    ArrayAdapter<String> chatMessageArrayAdapter;
    ImageView imgEmoji1;
    ImageView imgEmoji2;
    ImageView imgEmoji3;
    EditText editTextChat;
    String chatMsg = "";
    Button buttonSend;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        imgEmoji1 = (ImageView)view.findViewById(R.id.imgEmoji1);
        imgEmoji2 = (ImageView)view.findViewById(R.id.imgEmoji2);
        editTextChat = (EditText)view.findViewById(R.id.editTextChat);
        buttonSend = (Button)view.findViewById(R.id.buttonSend);

        chatMessageArrayList = new ArrayList<ChatMessage>();
        chatMessageArrayAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.chat_item_view);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMsg = editTextChat.getText().toString();
                Log.d("Chat message: ", chatMsg);
            }
        });

        imgEmoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageArrayList.add(new ChatMessage(1,1,chatMsg));
            }
        });




        return view;
    }
}
