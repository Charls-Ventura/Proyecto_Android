package com.example.proyecto_android.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.adapter.MessageAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerMessages;
    private TextInputEditText editTextMessage;

    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerMessages = findViewById(R.id.recyclerMessages);
        editTextMessage = findViewById(R.id.editTextMessage);

        setupRecyclerView();
    }

    private void setupRecyclerView() {

        messageAdapter = new MessageAdapter(
                new ArrayList<>(),
                ""
        );

        recyclerMessages.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerMessages.setAdapter(messageAdapter);
    }
}