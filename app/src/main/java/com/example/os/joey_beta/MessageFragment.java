package com.example.os.joey_beta;

import android.media.Image;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.os.joey_beta.Services.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OS on 10/05/2016.
 */
public class MessageFragment extends Fragment {

    private List<ChatMessage> chat = new ArrayList<ChatMessage>();
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

        imgEmoji1 = (ImageView) view.findViewById(R.id.imgEmoji1);
        imgEmoji2 = (ImageView) view.findViewById(R.id.imgEmoji2);
        editTextChat = (EditText) view.findViewById(R.id.editTextChat);
        buttonSend = (Button) view.findViewById(R.id.buttonSend);

        //chatMessageArrayList = new ArrayList<ChatMessage>();
        //chatMessageArrayAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.chat_item_view);

        final ArrayAdapter<ChatMessage> adapter = new MyListAdapter();
        final ListView list = (ListView) view.findViewById(R.id.chatListView);
        list.setAdapter(adapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMsg = editTextChat.getText().toString();
                adapter.add(new ChatMessage(1,R.drawable.empty_item, chatMsg));
                list.setAdapter(adapter);
                Log.d("Chat message: ", chatMsg);
            }
        });

        imgEmoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chatMessageArrayList.add(new ChatMessage(1,1,chatMsg));
                adapter.add(new ChatMessage(1,R.drawable.ic_rabbit_pin50,""));
                list.setAdapter(adapter);
            }
        });

        imgEmoji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(new ChatMessage(1,R.drawable.ic_bear_pin50,""));
                list.setAdapter(adapter);
            }
        });

        imgEmoji1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                adapter.add(new ChatMessage(2,R.drawable.joey_msg_emoji1,""));
                list.setAdapter(adapter);
                return false;
            }
        });


        return view;
    }

    private class MyListAdapter extends ArrayAdapter<ChatMessage> {
        public MyListAdapter() {
            super(getActivity(), R.layout.chat_item_view, chat);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.chat_item_view, parent, false);
            }

            // Find message
            ChatMessage currentChat = chat.get(position);

            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_chat);
            imageView.setImageResource(currentChat.getIconID());

            // Fill the Message Text
            TextView makeText = (TextView)itemView.findViewById(R.id.txt_chat);
            makeText.setText(currentChat.getMsg());

            return itemView;
        }
    }
}
