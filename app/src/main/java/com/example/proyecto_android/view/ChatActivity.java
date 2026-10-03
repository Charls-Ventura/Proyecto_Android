package com.example.proyecto_android.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView txtChatUserName = findViewById(R.id.txtChatUserName);
        RecyclerView recyclerMessages = findViewById(R.id.recyclerMessages);

        recyclerMessages.setLayoutManager(
                new LinearLayoutManager(this)
        );

        String userId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String userEmail = getIntent().getStringExtra("userEmail");

        if (userName != null) {
            txtChatUserName.setText(userName);
        }
    }
}