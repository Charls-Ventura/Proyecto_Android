package com.example.proyecto_android.view;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.adapter.MessageAdapter;
import com.example.proyecto_android.viewmodel.ChatViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_RECEIVER_ID = "receiver_id";
    public static final String EXTRA_RECEIVER_NAME = "receiver_name";

    private RecyclerView recyclerMessages;
    private TextInputEditText editTextMessage;
    private ImageButton buttonSend;
    private TextView textChatTitle;

    private MessageAdapter messageAdapter;
    private ChatViewModel chatViewModel;

    private String currentUserId;
    private String receiverId;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();

        if (!loadUsers()) {
            return;
        }

        setupRecyclerView();
        setupViewModel();
        setupSendButton();
    }

    private void initializeViews() {
        recyclerMessages = findViewById(R.id.recyclerMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        textChatTitle = findViewById(R.id.textChatTitle);
    }

    private boolean loadUsers() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "No hay una sesión activa.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return false;
        }

        currentUserId = currentUser.getUid();

        receiverId = getIntent().getStringExtra(
                EXTRA_RECEIVER_ID
        );

        receiverName = getIntent().getStringExtra(
                EXTRA_RECEIVER_NAME
        );

        if (receiverId == null || receiverId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "No se pudo identificar al destinatario.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return false;
        }

        if (receiverName != null &&
                !receiverName.trim().isEmpty()) {

            textChatTitle.setText(receiverName);
        }

        return true;
    }

    private void setupRecyclerView() {

        messageAdapter = new MessageAdapter(
                new ArrayList<>(),
                currentUserId
        );

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);

        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(messageAdapter);
    }

    private void setupViewModel() {

        chatViewModel =
                new ViewModelProvider(this)
                        .get(ChatViewModel.class);

        chatViewModel.getMessages().observe(
                this,
                messages -> {

                    if (messages == null) {
                        return;
                    }

                    messageAdapter.updateMessages(messages);

                    if (!messages.isEmpty()) {
                        recyclerMessages.scrollToPosition(
                                messages.size() - 1
                        );
                    }
                }
        );

        chatViewModel.getErrorMessage().observe(
                this,
                error -> {

                    if (error == null ||
                            error.trim().isEmpty()) {
                        return;
                    }

                    Toast.makeText(
                            this,
                            error,
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        chatViewModel.getMessageSent().observe(
                this,
                sent -> {

                    if (Boolean.TRUE.equals(sent)) {
                        editTextMessage.setText("");
                    }
                }
        );

        chatViewModel.startListeningForMessages(
                currentUserId,
                receiverId
        );
    }

    private void setupSendButton() {

        buttonSend.setOnClickListener(view -> {

            String text = "";

            if (editTextMessage.getText() != null) {
                text = editTextMessage
                        .getText()
                        .toString();
            }

            chatViewModel.sendMessage(
                    currentUserId,
                    receiverId,
                    text
            );
        });
    }
}