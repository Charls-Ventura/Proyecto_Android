package com.example.proyecto_android.repository;

import com.example.proyecto_android.model.Message;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
public class ChatRepository {

    private final FirebaseFirestore firestore;

    public ChatRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void sendMessage(Message message, MessageCallback callback) {

        String chatId = generateChatId(
                message.getSenderId(),
                message.getReceiverId()
        );

        firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    callback.onError(exception.getMessage());
                });
    }

    public ListenerRegistration listenForMessages(
            String currentUserId,
            String otherUserId,
            MessagesListener listener) {

        String chatId = generateChatId(
                currentUserId,
                otherUserId
        );

        return firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (snapshot == null) {
                        return;
                    }

                    List<Message> messages = new ArrayList<>();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        Message message = document.toObject(Message.class);

                        if (message != null) {
                            message.setId(document.getId());
                            messages.add(message);
                        }
                    }

                    listener.onMessagesChanged(messages);
                });
    }

    private String generateChatId(String userId1, String userId2) {

        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        }

        return userId2 + "_" + userId1;
    }

    public interface MessageCallback {

        void onSuccess();

        void onError(String message);
    }

    public interface MessagesListener {

        void onMessagesChanged(List<Message> messages);

        void onError(String message);
    }
}
